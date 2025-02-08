package com.cerebra.fileprocessor.service.impl;

import com.cerebra.fileprocessor.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyFileProcessStatusToMonitor(String userEmail, String message) {
        messagingTemplate.convertAndSendToUser(userEmail, "/topic/messages", message);
    }
}
