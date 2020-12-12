package com.example.spring_cloud_gateway_demo.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class DynamicRouteServiceInitialization implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private DynamicRouteServiceImpl dynamicRouteService;

    private static final long DEFAULT_TIMEOUT = 30000;

    private ConfigService configService;

    @Value("${spring.cloud.nacos.config.server-addr}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String nacosNamespace;

    @Value("${nacos.gateway.route.config.data-id}")
    private String nacosRouteDataId;

    @Value("${nacos.gateway.route.config.group}")
    private String nacosRouteGroup;

    private List<String> definitionIds = new CopyOnWriteArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("gateway route init...");
        try{
            configService = initConfigService();
            if(configService == null){
                log.warn("initConfigService fail");
                return;
            }
            String configInfo = configService.getConfig(nacosRouteDataId, nacosRouteGroup, DEFAULT_TIMEOUT);
            log.info("获取网关当前配置:\r\n{}",configInfo);
            List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
            for(RouteDefinition definition : definitionList){
                log.info("update route : {}",definition.toString());
                dynamicRouteService.add(definition);
                definitionIds.add(definition.getId());
            }
        } catch (Exception e) {
            log.error("初始化网关路由时发生错误",e);
        }
        dynamicRouteByNacosListener(nacosRouteDataId, nacosRouteGroup);
    }

    /**
     * 监听Nacos下发的动态路由配置
     * @param dataId
     * @param group
     */
    public void dynamicRouteByNacosListener (String dataId, String group){
        try {
            configService.addListener(dataId, group, new Listener()  {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("进行网关更新:\n\r{}",configInfo);
                    List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);

                    // 先根据 definitionId 删除所有旧的 definition
                    for (String id : definitionIds) {
                        dynamicRouteService.delete(id);
                    }
                    definitionIds.clear();

                    for(RouteDefinition definition : definitionList){
                        log.info("update route : {}",definition.toString());
                        dynamicRouteService.add(definition);
                        definitionIds.add(definition.getId());
                    }
                }
                @Override
                public Executor getExecutor() {
                    log.info("getExecutor\n\r");
                    return null;
                }
            });
        } catch (NacosException e) {
            log.error("从nacos接收动态路由配置出错!!!",e);
        }
    }

    /**
     * 初始化网关路由 nacos config
     * @return
     */
    private ConfigService initConfigService(){
        try{
            Properties properties = new Properties();
            properties.setProperty("serverAddr", nacosServerAddr);
            properties.setProperty("namespace", nacosNamespace);
            return configService = NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            log.error("初始化网关路由时发生错误",e);
            return null;
        }
    }
}