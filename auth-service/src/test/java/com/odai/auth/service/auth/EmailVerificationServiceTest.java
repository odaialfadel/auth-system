package com.odai.auth.service.auth;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.odai.auth.configuration.properties.CustomMailProperties;
import com.odai.auth.domain.model.User;
import com.odai.auth.domain.model.VerificationToken;
import com.odai.auth.service.auth.mail.MailSender;
import com.odai.auth.service.auth.mail.builder.DefaultEmailContentBuilder;
import com.odai.auth.service.auth.mail.model.EmailDetails;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link EmailVerificationService} class, which is responsible for sending email verification
 * messages to users. This test class uses GreenMail to simulate an SMTP server and verify email delivery.
 * Dependencies such as {@link VerificationTokenService}, {@link MailSender}, {@link DefaultEmailContentBuilder},
 * and {@link CustomMailProperties} are mocked to isolate the service's behavior.
 */
@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    private static final String SENDER = "no-reply@example.com";
    private static final String RECIPIENT = "user@example.com";
    private static final String TOKEN = "test-token-123";

    @Mock private VerificationTokenService verificationTokenService;
    @Mock private MailSender mailSender;
    @Mock private DefaultEmailContentBuilder emailBuilder;
    @Mock private CustomMailProperties mailProperties;

    @InjectMocks private EmailVerificationService emailService;

    private static GreenMail greenMail;

    @BeforeAll
    static void startGreenMail() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.setUser(SENDER, "pwd");
        greenMail.start();
    }

    @AfterAll
    static void stopGreenMail() {
        greenMail.stop();
    }

    @Test
    void testSendVerificationMail_sendsExpectedEmail() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail(RECIPIENT);

        // Only set up needed mocks for this test
        String subject = "Email Verification - TestService";
        String content = "<h1>Verify your email</h1><p>Token: " + TOKEN + "</p>";
        String template = "email/verify/verify-email";

        VerificationToken token = new VerificationToken();
        token.setToken(TOKEN);
        when(verificationTokenService.createToken(1L)).thenReturn(token);

        when(mailProperties.getFrom()).thenReturn(SENDER);
        CustomMailProperties.VerifyMail verifyProps = mock(CustomMailProperties.VerifyMail.class);
        when(mailProperties.getVerify()).thenReturn(verifyProps);
        when(verifyProps.getServiceName()).thenReturn("TestService");

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setFrom(SENDER);
        emailDetails.setTo(List.of(RECIPIENT));
        emailDetails.setSubject(subject);
        emailDetails.setContent(content);
        when(emailBuilder.build(eq(SENDER), eq(List.of(RECIPIENT)), eq(subject), eq(template), anyMap()))
                .thenReturn(emailDetails);

        doAnswer(invocation -> {
            sendViaGreenMail(invocation.getArgument(0));
            return null;
        }).when(mailSender).sendMail(any());

        // Act
        emailService.sendVerificationMail(user);

        // Assert
        assertTrue(greenMail.waitForIncomingEmail(5000, 1), "No email received");
        MimeMessage msg = greenMail.getReceivedMessages()[0];
        assertEquals(SENDER, msg.getFrom()[0].toString());
        assertEquals(RECIPIENT, msg.getAllRecipients()[0].toString());
        assertEquals(subject, msg.getSubject());
        assertTrue(GreenMailUtil.getBody(msg).contains(TOKEN));
    }

    private void sendViaGreenMail(EmailDetails email) throws Exception {
        MimeMessage msg = new MimeMessage(greenMail.getSmtp().createSession());
        msg.setFrom(email.getFrom());
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo().getFirst()));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getContent(), "text/html");
        Transport.send(msg);
    }
}