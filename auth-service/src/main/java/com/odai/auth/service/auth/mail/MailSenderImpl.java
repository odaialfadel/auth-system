package com.odai.auth.service.auth.mail;

import com.odai.auth.service.auth.mail.model.EmailDetails;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link MailSender} using Spring's {@link JavaMailSender}.
 * <p>
 * This service sends emails with support for HTML content, multiple recipients, BCC, and UTF-8 encoding.
 * </p>
 */
@Service
@AllArgsConstructor
public class MailSenderImpl implements MailSender {

    private final JavaMailSender javaMailSender;

    /**
     * Sends an email with the details specified in the {@link EmailDetails} object.
     * <p>
     * Supports setting from address, multiple recipients, optional BCC recipients,
     * subject line, and HTML content.
     * </p>
     *
     * @param emailDetails the email details including recipients, subject, and content
     */
    @Override
    public void sendMail(EmailDetails emailDetails) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setFrom(emailDetails.getFrom());
            helper.setTo(emailDetails.getTo().toArray(new String[0]));
            if (emailDetails.getBcc() != null) {
                helper.setBcc(emailDetails.getBcc().toArray(new String[0]));
            }
            helper.setSubject(emailDetails.getSubject());
            helper.setText(emailDetails.getContent(), true);
        });
    }
}
