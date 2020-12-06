package com.example.spring_cloud_gateway_demo.conf;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Created by jiashen on 2020/5/20.
 */
//@Configuration
//public class GatewayRoutes {
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//            .route("test1", r -> r.path("/test1")
//                .filters(f -> f.stripPrefix(1))
////                .retry(3)
////                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .setStatus(HttpStatus.BAD_REQUEST))
//                .uri("http://127.0.0.1:18083/consumer")
//                .order(1))
//
//            .build();
//    }
//}
