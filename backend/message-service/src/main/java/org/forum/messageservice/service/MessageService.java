package org.forum.messageservice.service;

import org.forum.messageservice.dto.request.ContactUsRequest;
import org.forum.messageservice.dto.response.GeneralResponse;
import org.forum.messageservice.entity.Message;
import org.forum.messageservice.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageService {
    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public GeneralResponse contactUs(ContactUsRequest dto, String token) {
        messageRepository.createMessage(dto, token);
        return GeneralResponse.builder()
                .message("We have forwarded your messages to admin!")
                .build();
    }

    public GeneralResponse getAllMessages() {
        List<Message> list = messageRepository.getAllMessages();
        return GeneralResponse.builder()
                .message("All messages")
                .data(list)
                .build();
    }

    public GeneralResponse changeStatus(int message_id) {
        messageRepository.changeStatus(message_id);
        return GeneralResponse.builder()
                .message("Status changed!")
                .build();
    }
}
