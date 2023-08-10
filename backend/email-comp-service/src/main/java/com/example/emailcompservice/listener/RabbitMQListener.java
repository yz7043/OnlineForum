package com.example.emailcompservice.listener;


import com.example.emailcompservice.entity.EmailMessage;
import com.example.emailcompservice.service.EmailCompService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListener {
    private final EmailCompService emailCompService;

    public RabbitMQListener(EmailCompService emailCompService) {
        this.emailCompService = emailCompService;
    }

    @RabbitListener(queues = "emailQueue")
    public void handleMessage(EmailMessage emailMessage) {
        emailCompService.sendMail(emailMessage);
    }
}

