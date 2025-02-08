package com.cerebra.fileprocessor.service.impl;

import com.cerebra.fileprocessor.common.ResponseUtil;
import com.cerebra.fileprocessor.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl  implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class.getName());
    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendEmailLoginCredentials(String name, String email, String password, String sendTo, String message) {
        try {
            if (StringUtils.isBlank(message)) {
                message = "Thank you for registering with our service. We are excited to have you on board.";
            }
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(sendTo);
            helper.setSubject(String.format("Welcome %s – Your Login Credentials.", name));
            String filePath = "templates/user-login-credentials-template.html";
            String htmlContent = loadTemplate(Map.of(
                    "{message}", message,
                    "{name}", name,
                    "{date}", ResponseUtil.getCurrentDate(),
                    "{username}", email,
                    "{password}", password
            ), filePath);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            LOGGER.info("Login credentials email sent to: {}", sendTo);
        } catch (Exception e) {
            LOGGER.error("Error sending login credentials email to {}: {}", sendTo, e.getMessage());
        }
    }


    private String loadTemplate(Map<String, String> replacements, String templateId) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templateId);
        String content = "Empty";
        try {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            content = getTemplate(replacements, content);
        } catch (IOException e) {
            LOGGER.error("Invalid Template {}: {}", templateId, e.getMessage());
        }
        return content;
    }


    private String getTemplate(Map<String, String> replacements, String cTemplate) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            cTemplate = cTemplate.replace(entry.getKey(), entry.getValue());
        }
        return cTemplate;
    }
}
