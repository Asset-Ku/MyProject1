package com.example.demo.service;

import com.example.demo.entity.Message;
import com.example.demo.entity.Users;
import com.example.demo.entity.dto.MessageDto;
import com.example.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    public Page<MessageDto> messageList(Pageable pageable, String filter, Users users) {
        if (filter != null && !filter.isEmpty())
            return messageRepository.findByTag(filter, pageable,users);
        else
            return messageRepository.findAll(pageable,users);
    }

    public Page<MessageDto> messageListForUser(Pageable pageable,  Users currentUser, Users author) {
        return messageRepository.findByUser(pageable,author,currentUser);
    }
}
