package com.nhnacademy.workentry.adapter.notify.dto;

import com.nhnacademy.workentry.adapter.notify.config.RabbitMQConfig;
import com.nhnacademy.workentry.entry.email.dto.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSender {
    private final RabbitTemplate rabbitTemplate;

    public EmailSender(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmail(EmailRequest emailRequest){
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, emailRequest);
        log.info("[#] Sent Email : {}", emailRequest);
    }


}
