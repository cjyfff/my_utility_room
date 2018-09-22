package com.cjyfff.dq.task.service;

import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 18-8-17.
 */
@Service
public class PublicMsgService {

    public void acceptMsg() {
        if (hasLeaderShip()) {
            handlerMsg();
        } else {
            redirectMsg();
        }
    }

    private boolean hasLeaderShip() {
        return true;
    }

    private void handlerMsg() {}

    private void redirectMsg() {}
}
