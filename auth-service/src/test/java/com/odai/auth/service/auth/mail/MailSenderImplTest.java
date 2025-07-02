package com.odai.auth.service.auth.mail;


import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.odai.auth.service.auth.mail.model.EmailDetails;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link MailSenderImpl} class, which is responsible for sending emails using a configured
 * {@link JavaMailSenderImpl}. This test class uses GreenMail, an in-memory SMTP server, to simulate email sending
 * and verify email delivery. The tests cover scenarios with and without BCC recipients, ensuring proper email
 * construction and delivery.
 */
class MailSenderImplTest {

    private static final String SENDER = "sender@example.com";
    private static final String RECIPIENT_1 = "recipient1@example.com";
    private static final String RECIPIENT_2 = "recipient2@example.com";
    private static final String BCC = "bcc@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_EMAIL_CONTENT_HTML = "<h1>Test Email Content</h1>";

    private GreenMail greenMail;
    private MailSenderImpl mailSender;

    @BeforeEach
    void setUp() {
        // Start GreenMail SMTP server
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();

        // Configure JavaMailSender with GreenMail's SMTP settings
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("localhost");
        javaMailSender.setPort(ServerSetupTest.SMTP.getPort());
        javaMailSender.setProtocol("smtp");
        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");

        // Initialize MailSenderImpl with the configured JavaMailSender
        mailSender = new MailSenderImpl(javaMailSender);
    }

    @AfterEach
    void tearDown() {
        // Stop GreenMail server
        if (greenMail != null) {
            greenMail.stop();
        }
    }

    /**
     * Tests the {@link MailSenderImpl#sendMail(EmailDetails)} method with an email containing multiple
     * recipients and a BCC. Verifies that the email is sent successfully, received by all recipients
     * (including BCC), and contains the correct sender, subject, and content.
     *
     * @throws Exception if an error occurs during email sending or verification
     */
    @Test
    void testSendMail() throws Exception {
        // Arrange
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setFrom(SENDER);
        emailDetails.setTo(List.of(RECIPIENT_1, RECIPIENT_2));
        emailDetails.setBcc(List.of(BCC));
        emailDetails.setSubject(TEST_SUBJECT);
        emailDetails.setContent(TEST_EMAIL_CONTENT_HTML);

        // Act
        mailSender.sendMail(emailDetails);

        // Assert
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(3, receivedMessages.length);

        MimeMessage receivedMessage = receivedMessages[0];

        // Verify email details
        assertEquals(SENDER, receivedMessage.getFrom()[0].toString());
        assertEquals(RECIPIENT_1, receivedMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(RECIPIENT_2, receivedMessage.getRecipients(MimeMessage.RecipientType.TO)[1].toString());
        assertTrue(wasEmailReceivedByBcc(BCC));
        assertEquals(TEST_SUBJECT, receivedMessage.getSubject());
        assertTrue(GreenMailUtil.getBody(receivedMessage).contains(TEST_EMAIL_CONTENT_HTML));

    }

    /**
     * Verifies whether an email was received by the specified BCC recipient. Since GreenMail does not
     * expose BCC recipients directly in the received message (as BCC is meant to be hidden), this method
     * checks the inbox of the BCC user to confirm email receipt.
     *
     * @param bccEmail the email address of the BCC recipient
     * @return {@code true} if the BCC recipient received the email, {@code false} otherwise
     */
    private boolean wasEmailReceivedByBcc(String bccEmail) {
        try {
            GreenMailUser bccUser = greenMail.getUserManager().getUser(bccEmail);
            MailFolder inbox = greenMail.getManagers().getImapHostManager().getInbox(bccUser);
            return !inbox.getMessages().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Tests the {@link MailSenderImpl#sendMail(EmailDetails)} method with an email that does not include
     * BCC recipients. Verifies that the email is sent successfully, received by the single recipient,
     * and contains the correct sender, subject, and content, with no BCC recipients.
     *
     * @throws Exception if an error occurs during email sending or verification
     */
    @Test
    void testSendMailWithoutBcc() throws Exception {
        // Arrange
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setFrom(SENDER);
        emailDetails.setTo(List.of(RECIPIENT_1));
        emailDetails.setBcc(null);
        emailDetails.setSubject(TEST_SUBJECT);
        emailDetails.setContent(TEST_EMAIL_CONTENT_HTML);

        // Act
        mailSender.sendMail(emailDetails);

        // Assert
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage receivedMessage = receivedMessages[0];

        // Verify email details
        assertEquals(SENDER, receivedMessage.getFrom()[0].toString());
        assertEquals(RECIPIENT_1, receivedMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertNull(receivedMessage.getRecipients(MimeMessage.RecipientType.BCC));
        assertEquals(TEST_SUBJECT, receivedMessage.getSubject());
        assertTrue(GreenMailUtil.getBody(receivedMessage).contains(TEST_EMAIL_CONTENT_HTML));
    }
}