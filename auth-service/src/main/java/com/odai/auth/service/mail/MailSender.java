package com.odai.auth.service.mail;

import com.odai.auth.service.mail.model.EmailDetails;

public interface MailSender {
    void sendMail(EmailDetails emailDetails);
}
