package com.cerebra.fileprocessor;

import com.cerebra.fileprocessor.config.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication(scanBasePackages = "com.cerebra")
@EnableJpaRepositories(basePackages = "com.cerebra")
@EntityScan(basePackages = "com.cerebra")
@ComponentScan(basePackages = "com.cerebra")
@EnableTransactionManagement
@EnableAsync
@EnableConfigurationProperties(ConfigProperties.class)
public class CerebraApplication {

	public static void main(String[] args) {
		SpringApplication.run(CerebraApplication.class, args);
	}
}