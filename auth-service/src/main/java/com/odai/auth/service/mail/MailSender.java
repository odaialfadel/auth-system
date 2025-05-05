package com.odai.auth.service.mail;

import jakarta.mail.internet.MimeMessage;

public interface MailSender {
    void sendMail(String to, String subject, MimeMessage mimeMessage);
}
