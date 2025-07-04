package com.odai.auth.service.auth.mail.builder;

import com.odai.auth.service.auth.mail.model.EmailDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Map;

/**
 * Component responsible for building email content using Thymeleaf templates.
 * <p>
 * This class uses the provided template engine to process HTML or text templates,
 * injecting dynamic variables into the template context.
 * </p>
 */
@RequiredArgsConstructor
@Component
public class DefaultEmailContentBuilder {

    private final ITemplateEngine templateEngine;

    /**
     * Builds an {@link EmailDetails} instance by processing a mail template with variables.
     * <p>
     * The method sets the sender, recipients, subject, and generates the email content
     * by applying the variables to the specified template.
     * </p>
     *
     * @param from         the email address of the sender
     * @param to           list of recipient email addresses
     * @param subject      the subject of the email
     * @param mailTemplate the name/path of the Thymeleaf template to process
     * @param variables    a map of variables to be injected into the template context; can be null
     * @return a fully constructed {@link EmailDetails} object ready for sending
     */
    public EmailDetails build(String from, List<String> to, String subject, String mailTemplate, Map<String, Object> variables) {
        return EmailDetails.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .content(adaptedMailTemplate(mailTemplate, variables))
                .build();
    }

    /**
     * Processes the given mail template with the provided variables to produce the email content.
     *
     * @param mailTemplate the template name/path
     * @param variables    the variables to inject into the template context; may be null
     * @return the rendered email content as a String
     */
    private String adaptedMailTemplate(String mailTemplate, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }

        return templateEngine.process(mailTemplate, context);
    }
}
