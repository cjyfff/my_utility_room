package com.cjyfff.dq.election;

import java.net.InetAddress;
import java.util.Map;
import java.util.Map.Entry;

import com.cjyfff.dq.election.exception.ElectionException;
import com.cjyfff.dq.election.info.ShardingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/9/8.
 */
@Component
public class ElectionUtils {

    @Autowired
    private ShardingInfo shardingInfo;

    @Value("${server.port}")
    private String servicePort;

    public void updateSelfShardingInfo(Map<Integer, String> shardingMap) throws Exception {
        if (shardingMap == null) {
            shardingInfo.setShardingMap(null);
            return;
        }

        InetAddress addr = InetAddress.getLocalHost();
        String host = addr.getHostAddress() + ":" + servicePort;

        shardingInfo.setShardingMap(shardingMap);

        Integer nodeId = null;
        for (Entry<Integer, String> node : shardingMap.entrySet()) {
            if (host.equals(node.getValue())) {
                nodeId = node.getKey();
                break;
            }
        }
        if (nodeId == null) {
            throw new ElectionException("Invalid Sharding Map, can not find self node info.");
        }

        shardingInfo.setNodeId(nodeId);
    }
}
