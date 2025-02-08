package com.cerebra.fileprocessor.socket;

import com.cerebra.fileprocessor.config.ConfigProperties;
import com.cerebra.fileprocessor.service.NotificationService;
import com.sun.security.auth.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final NotificationService notificationService;
    private final ConfigProperties configProperties;
    public WebSocketConfig(@Lazy NotificationService notificationService, ConfigProperties configProperties) {
        this.notificationService = notificationService;
        this.configProperties=configProperties;
    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/notify/");
        registry.setApplicationDestinationPrefixes("/ws");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/cerebra-notification").setHandshakeHandler(new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                              Map<String, Object> attributes) {
                String activeUser = "";
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
                    activeUser = httpServletRequest.getParameter("activeUser");
                }
                return new UserPrincipal(activeUser);
            }

        }).setAllowedOrigins(configProperties.getStompAccessUrl().split(",")).withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                String userDetail = accessor.getHeader("simpUser").toString().replace("String.", "");
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Schedule message sending after 1 minute
                    Executors.newScheduledThreadPool(1).schedule(() -> {
                        notificationService.notifyFileProcessStatusToMonitor(userDetail,"");
                    }, 5, TimeUnit.SECONDS);
                }
                return message;
            }
        });
    }

}
