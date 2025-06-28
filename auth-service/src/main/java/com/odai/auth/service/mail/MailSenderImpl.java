package com.odai.auth.service.mail;

import com.odai.auth.service.mail.model.EmailDetails;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class MailSenderImpl implements MailSender {

    private final JavaMailSender javaMailSender;

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
