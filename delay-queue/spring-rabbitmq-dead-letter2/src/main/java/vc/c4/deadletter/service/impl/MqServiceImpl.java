package vc.c4.deadletter.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vc.c4.deadletter.mq.DeadLetterSendReceive;
import vc.c4.deadletter.mq.DeadLetterSendReceive2;
import vc.c4.deadletter.service.MqService;

/**
 * Created by jiashen on 18-7-30.
 */
@Service
public class MqServiceImpl implements MqService {

    //@Autowired
    //private DeadLetterSendReceive deadLetterSendReceive;
    //
    //@Autowired
    //private DeadLetterSendReceive2 deadLetterSendReceive2;
    //
    //public void init() {
    //    deadLetterSendReceive.sender();
    //    deadLetterSendReceive2.sender();
    //}
}
