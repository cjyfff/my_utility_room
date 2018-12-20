package com.cjyfff.dq.task.biz;

import com.cjyfff.dq.election.biz.NoneBiz;
import org.springframework.stereotype.Component;

/**
 * slave选举完成后执行的逻辑：
 * 1、初次启动，重启后把数据库中`队列中`状态的任务加载到队列，防止重启后内存队列中的数据丢失
 * Created by jiashen on 18-12-11.
 */
@Component
public class SlaveAfterUpdateElectionFinishBiz extends NoneBiz {
}
