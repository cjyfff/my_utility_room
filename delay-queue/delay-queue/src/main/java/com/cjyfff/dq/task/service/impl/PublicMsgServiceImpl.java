package com.cjyfff.dq.task.service.impl;

import java.util.Date;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.ApiException;
import com.cjyfff.dq.task.common.DefaultWebApiResult;
import com.cjyfff.dq.task.common.HttpUtils;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.queue.QueueTask;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import com.cjyfff.dq.task.vo.dto.InnerMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2018/9/23.
 */
@Slf4j
@Service
public class PublicMsgServiceImpl implements PublicMsgService {

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ExecLogComponent execLogComponent;

    @Value("${delay_queue.critical_polling_time}")
    private Long criticalPollingTime;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(AcceptMsgDto reqDto) throws Exception {
        acceptTaskComponent.checkElectionStatus();

        DelayTask newDelayTask = createTask(reqDto);

        // 因为 reqDto 的 task id 和 delayTask 的 task id 不一样，为防止之后的代码取错，
        // 这里把 reqDto 设为 null
        reqDto = null;

        if (acceptTaskComponent.checkIsMyTask(newDelayTask.getTaskId())) {
            if (checkNeedToPushQueueNow(newDelayTask.getDelayTime())) {
                QueueTask task = new QueueTask(
                    newDelayTask.getTaskId(), newDelayTask.getFunctionName(), newDelayTask.getParams(),
                    newDelayTask.getDelayTime()
                );
                acceptTaskComponent.pushToQueue(task);
                newDelayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
                newDelayTask.setModifiedAt(new Date());
                delayTaskMapper.updateByPrimaryKeySelective(newDelayTask);

                execLogComponent.insertLog(newDelayTask, TaskStatus.IN_QUEUE.getStatus(),
                    String.format("In Queue: %s", newDelayTask.getTaskId()));

            }
        } else {
            // 转发到对应机器
            try {
                Integer targetShardingId = acceptTaskComponent.getShardingIdFormTaskId(newDelayTask.getTaskId());
                String targetHost = shardingInfo.getShardingMap().get(targetShardingId);

                if (targetHost == null) {
                    throw new ApiException("-201",
                        String.format("任务转发时找不到对应的分片信息, sharding Id: %s", targetShardingId.toString()));
                }

                String url = String.format("http://%s/dq/acceptInnerMsg", targetHost);
                InnerMsgDto innerMsgDto = new InnerMsgDto();

                newDelayTask.setStatus(TaskStatus.TRANSMITING.getStatus());
                newDelayTask.setModifiedAt(new Date());
                delayTaskMapper.updateByPrimaryKeySelective(newDelayTask);

                BeanUtils.copyProperties(newDelayTask, innerMsgDto);

                sendInnerTaskMsg(url, innerMsgDto, targetShardingId, targetHost);
            } catch (Exception err) {
                // 转发逻辑即时报错也不抛出异常，由定时任务做补偿操作
                log.error("Send inner task get error: ", err);
            }
        }

    }

    @Async
    public void sendInnerTaskMsg(String url, InnerMsgDto innerMsgDto,
                                 Integer targetShardingId, String targetHost) {
        String resultJson = HttpUtils.doPost(url, JSON.toJSONString(innerMsgDto));

        log.info(String.format("Send inner task msg to node id :%s, host: %s, resp is %s",
            targetShardingId, targetHost, resultJson));

        DefaultWebApiResult result = JSON.parseObject(resultJson, DefaultWebApiResult.class);
        if (!DefaultWebApiResult.SUCCESS_CODE.equals(result.getCode())) {
            log.error("Send inner task get error: " + result.getMsg());
        }
    }

    /**
     * 根据入参中的delayTime判断任务是不是需要马上入队
     * @param delayTime
     * @return
     */
    private boolean checkNeedToPushQueueNow(Long delayTime) {
        return delayTime.compareTo(criticalPollingTime) <= 0;
    }


    /**
     * 任务持久化
     * 为了简化逻辑，createTask不新建事务，出现异常都回滚，由调用方重试
     * 因为程序总会有概率的出现异常的，假如出现异常时，任务已经创建，并且通知到调用方的话，
     * 调用方会重复发送请求，这样的话任务就会重复被创建
     * @param reqDto
     */
    private DelayTask createTask(AcceptMsgDto reqDto) {
        DelayTask delayTask = new DelayTask();
        int ranInt = (int)(Math.random() * 90000) + 10000;

        final String finalTaskId = reqDto.getTaskId() + "-" + ranInt;
        delayTask.setTaskId(finalTaskId);
        delayTask.setFunctionName(reqDto.getFunctionName());
        delayTask.setParams(reqDto.getParams());
        delayTask.setRetryCount(reqDto.getRetryCount());
        delayTask.setRetryInterval(reqDto.getRetryInterval());
        delayTask.setDelayTime(reqDto.getDelayTime());
        delayTask.setExecuteTime(System.currentTimeMillis() / 1000 + reqDto.getDelayTime());
        delayTask.setStatus(TaskStatus.ACCEPT.getStatus());
        delayTask.setCreatedAt(new Date());
        delayTask.setModifiedAt(new Date());
        delayTask.setShardingId(acceptTaskComponent.getShardingIdFormTaskId(finalTaskId).byteValue());

        delayTaskMapper.insert(delayTask);

        execLogComponent.insertLog(delayTask, TaskStatus.ACCEPT.getStatus(), String.format("Insert task: %s", finalTaskId));

        return delayTask;
    }
}
