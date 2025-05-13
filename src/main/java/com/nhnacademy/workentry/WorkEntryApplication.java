package com.nhnacademy.workentry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories
@EnableScheduling
public class WorkEntryApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkEntryApplication.class, args);

    }
}
