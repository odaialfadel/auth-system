package com.odai.auth.service.auth.mail.model;

import jakarta.activation.DataSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * Data transfer object representing the details of an email message.
 * <p>
 * Contains sender and recipient information, subject, body content,
 * and optional attachments.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetails {

    private String from;
    private List<String> to;
    private List<String> bcc;
    private String subject;
    private String content;
    private Collection<EmailAttachment> attachments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailAttachment {
        private String name;
        private DataSource dataSource;
    }
}
