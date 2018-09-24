package com.cjyfff.dq.task.service.impl;

import java.util.Date;

import com.cjyfff.dq.election.info.ElectionStatus;
import com.cjyfff.dq.task.common.TaskStatus;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import com.cjyfff.dq.task.utils.AcceptTaskComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2018/9/23.
 */
@Service
public class PublicMsgServiceImpl implements PublicMsgService {

    @Autowired
    private ElectionStatus electionStatus;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(AcceptMsgDto reqDto) throws Exception {
        acceptTaskComponent.checkElectionStatus();

        createTask(reqDto);



    }

    private void  createTask(AcceptMsgDto reqDto) {
        DelayTask delayTask = new DelayTask();
        int ranInt = (int)(Math.random() * 90000) + 10000;

        final String finalTaskId = reqDto.getTaskId() + "-" + ranInt;
        delayTask.setTaskId(finalTaskId);
        delayTask.setFunctionName(reqDto.getFunctionName());
        delayTask.setParams(reqDto.getParams());
        delayTask.setRetryCount(reqDto.getRetryCount());
        delayTask.setRetryInterval(reqDto.getRetryInterval());
        delayTask.setStatus(TaskStatus.ACCEPT.getStatus());
        delayTask.setCreatedAt(new Date());
        delayTask.setModifiedAt(new Date());
        delayTask.setShardingId(acceptTaskComponent.getShardingIdFormTaskId(finalTaskId).byteValue());

        delayTaskMapper.insert(delayTask);
    }
}
