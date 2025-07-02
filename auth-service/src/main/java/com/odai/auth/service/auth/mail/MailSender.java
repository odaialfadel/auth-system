package com.odai.auth.service.auth.mail;

import com.odai.auth.service.auth.mail.model.EmailDetails;

public interface MailSender {
    void sendMail(EmailDetails emailDetails);
}
