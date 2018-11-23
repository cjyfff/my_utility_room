package com.cjyfff.dq.monitor.controller;

import com.cjyfff.dq.monitor.controller.vo.NodeInfoVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 18-11-23.
 */
@RestController
public class MonitorController {

    @RequestMapping(path = "/monitor/nodeInfo", method={RequestMethod.GET})
    public NodeInfoVo getNodeInfoVo() {
        return new NodeInfoVo();
    }
}
