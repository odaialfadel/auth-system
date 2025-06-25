package com.odai.auth.service.mail.builder;

import com.odai.auth.service.mail.model.EmailDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DefaultEmailContentBuilder {

    private final ITemplateEngine templateEngine;

    public EmailDetails build(String from, List<String> to, String subject, String mailTemplate, Map<String, Object> variables) {
        return EmailDetails.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .content(adaptedMailTemplate(mailTemplate, variables))
                .build();
    }

    private String adaptedMailTemplate(String mailTemplate, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }

        return templateEngine.process(mailTemplate, context);
    }

}
