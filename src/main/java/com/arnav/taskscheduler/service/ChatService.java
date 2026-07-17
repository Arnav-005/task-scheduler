package com.arnav.taskscheduler.service;

import com.arnav.taskscheduler.model.ChatMessage;
import com.arnav.taskscheduler.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage saveMessage(String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setRole(role);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    // findAll() doesn't guarantee ordering, so we sort by timestamp explicitly -
    // this history is what makes the chat context-aware instead of stateless.
    public List<ChatMessage> getHistory() {
        return chatMessageRepository.findAll().stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                .collect(Collectors.toList());
    }
}
