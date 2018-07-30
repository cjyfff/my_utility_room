package vc.c4.deadletter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import vc.c4.deadletter.mq.DeadLetterSendReceive;
import vc.c4.deadletter.mq.DeadLetterSendReceive2;

/**
 * Created by jiashen on 18-7-30.
 */
@RestController
@RequestMapping("/")
public class MqController {

    @Autowired
    private DeadLetterSendReceive deadLetterSendReceive;

    @Autowired
    private DeadLetterSendReceive2 deadLetterSendReceive2;

    @RequestMapping(value = "/test01/", method = RequestMethod.GET)
    String test01() {
        deadLetterSendReceive.sender();
        deadLetterSendReceive2.sender();

        return "hello";
    }
}
