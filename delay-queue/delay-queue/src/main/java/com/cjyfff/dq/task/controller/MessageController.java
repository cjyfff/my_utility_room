package com.cjyfff.dq.task.controller;

import com.cjyfff.dq.task.common.ApiException;
import com.cjyfff.dq.task.common.DefaultWebApiResult;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 18-8-17.
 */
@RestController
@Slf4j
public class MessageController {

    @Autowired
    private PublicMsgService publicMsgService;

    /**
     * 接收外部消息
     */
    public DefaultWebApiResult acceptMsg(AcceptMsgDto reqDto) {
        try {
            publicMsgService.acceptMsg(reqDto);
            return DefaultWebApiResult.success();
        } catch (ApiException ae) {
            return DefaultWebApiResult.failure(ae.getCode(), ae.getMsg());
        } catch (Exception e) {
            log.error("publicMsgService acceptMsg get error: ", e);
            return DefaultWebApiResult.failure("-1", "system error");
        }
    }

    /**
     * 接收内部转发消息
     */
    public void acceptInnerMsg() {}
}
