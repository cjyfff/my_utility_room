package com.example.netty_ws_test.controller;

import com.example.netty_ws_test.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 2020/3/31.
 */
@RestController
@RequestMapping("/push")
public class PushController {

    @Autowired
    private PushService pushService;

    /**
     * 推送给所有用户
     * @param msg
     */
    @PostMapping("/pushAll")
    public String pushToAll(@RequestParam("msg") String msg){
        pushService.pushMsgToAll(msg);
        return "ok";
    }
    /**
     * 推送给指定用户
     * @param userId
     * @param msg
     */
    @PostMapping("/pushOne")
    public String pushMsgToOne(@RequestParam("userId") String userId, @RequestParam("msg") String msg){
        pushService.pushMsgToOne(userId,msg);
        return "ok";
    }

}

