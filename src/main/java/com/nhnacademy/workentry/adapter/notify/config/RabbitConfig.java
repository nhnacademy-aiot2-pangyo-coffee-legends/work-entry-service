package com.nhnacademy.workentry.adapter.notify.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 관련 설정을 담당하는 Configuration 클래스입니다.
 */
@Configuration
public class RabbitConfig {

    @Value("${email.queue}")
    private String emailQueue;

    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueue, true);
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange("email-exchange");
    }

    @Bean
    public Binding binding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(emailExchange)
                .with("email-routing-key");
    }

}