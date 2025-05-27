package com.nhnacademy.workentry.notify.dto;

import com.nhnacademy.workentry.notify.config.RabbitMQConfig;
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
        emailRequest.setType("TEXT");
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, emailRequest);
        log.info("[#] Sent Email : {}", emailRequest);
    }


}
