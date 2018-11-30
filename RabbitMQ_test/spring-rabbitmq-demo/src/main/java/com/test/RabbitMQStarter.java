
package com.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RabbitMQStarter {
	public static void main(String[] args) {
		new SpringApplicationBuilder(RabbitMQStarter.class).run(args);
	}
}
