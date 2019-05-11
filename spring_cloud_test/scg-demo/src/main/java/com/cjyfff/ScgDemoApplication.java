package com.cjyfff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ScgDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScgDemoApplication.class, args);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route(p -> p
				.path("/blog_api/all_comments/")
				.filters(f -> f.addRequestHeader("Hello", "World"))
				.uri("http://127.0.0.1:8888"))
			.route(p -> p
				.path("/dq/acceptMsg")
				.filters(f -> f.addRequestHeader("Hello", "World"))
				.uri("http://127.0.0.1:8877"))
			.build();
	}

	@RequestMapping("/fallback")
	public Mono<String> fallback() {
		return Mono.just("fallback");
	}
}
