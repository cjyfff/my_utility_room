package com.cjyfff.election;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-8-25.
 */
@Getter
@Setter
@Component
public class ShardingInfo {
    /**
     * 集群所有机器的分片信息，key为node id，value为机器ip
     */
    private Map<Integer, String> shardingMap;
}
