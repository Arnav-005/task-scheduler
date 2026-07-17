package com.arnav.taskscheduler.controller;

import com.arnav.taskscheduler.dto.ChatMessageDto;
import com.arnav.taskscheduler.dto.ChatRequestDto;
import com.arnav.taskscheduler.dto.ChatResponseDto;
import com.arnav.taskscheduler.dto.DtoMapper;
import com.arnav.taskscheduler.service.AiPlanningService;
import com.arnav.taskscheduler.service.ChatService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    private final AiPlanningService aiPlanningService;
    private final ChatService chatService;

    public ChatController(AiPlanningService aiPlanningService, ChatService chatService) {
        this.aiPlanningService = aiPlanningService;
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        String reply = aiPlanningService.respondToChat(request.message());
        return new ChatResponseDto(reply);
    }

    @GetMapping("/history")
    public List<ChatMessageDto> history() {
        return DtoMapper.toChatDtoList(chatService.getHistory());
    }
}
