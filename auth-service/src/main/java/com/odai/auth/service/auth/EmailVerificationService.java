package com.odai.auth.service.auth;

import com.odai.auth.domain.model.VerificationToken;
import com.odai.auth.service.mail.MailSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EmailVerificationService {

    private final VerificationTokenService verificationTokenService;
    private final MailSender mailSender;

    public void sendVerificationMail(String userId,String to, String subject, String htmlMail) {
        VerificationToken generatedToken = verificationTokenService.createToken(userId);
//        mailSender.sendMail(to, subject, );
    }
}
