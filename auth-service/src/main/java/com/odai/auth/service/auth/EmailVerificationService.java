package com.odai.auth.service.auth;

import com.odai.auth.configuration.properties.CustomMailProperties;
import com.odai.auth.domain.model.User;
import com.odai.auth.domain.model.VerificationToken;
import com.odai.auth.service.auth.mail.MailSender;
import com.odai.auth.service.auth.mail.builder.DefaultEmailContentBuilder;
import com.odai.auth.service.auth.mail.model.EmailDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service responsible for sending email verification messages to newly created users.
 * <p>
 * This service generates a verification token for the user and sends an email
 * containing the token using configured email content and mailing infrastructure.
 * </p>
 */
@Service
@AllArgsConstructor
public class EmailVerificationService {

    private final VerificationTokenService verificationTokenService;
    private final MailSender mailSender;
    private final DefaultEmailContentBuilder defaultEmailContentBuilder;
    private final CustomMailProperties mailProperties;

    /**
     * Sends a verification email to the specified user.
     * <p>
     * If the user has no email address, this method returns immediately without sending mail.
     * Otherwise, it creates a verification token, builds the email content, and sends the email.
     * </p>
     *
     * @param createdUser the user who just got created and needs email verification
     */
    public void sendVerificationMail(User createdUser) {
        if (createdUser.getEmail() == null) {
            return;
        }

        VerificationToken generatedToken = verificationTokenService.createToken(createdUser.getId());

        EmailDetails emailDetails = defaultEmailContentBuilder.build(
                mailProperties.getFrom(),
                List.of(createdUser.getEmail()),
                "Email Verification - " + mailProperties.getVerify().getServiceName(),
                "email/verify/verify-email",
                Map.of("token", generatedToken.getToken())
        );

        mailSender.sendMail(emailDetails);
    }
}
