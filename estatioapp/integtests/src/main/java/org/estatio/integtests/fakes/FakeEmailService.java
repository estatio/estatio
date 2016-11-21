package org.estatio.integtests.fakes;

import java.util.List;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.email.EmailService;

import lombok.Getter;

@DomainService(
        nature = NatureOfService.DOMAIN,
        menuOrder = "1"
)
public class FakeEmailService implements EmailService {

    public String getId() {
        return getClass().getName();
    }

    @PostConstruct
    @Override
    public void init() {
    }

    @Getter
    private List<String> to;
    @Getter
    private List<String> cc;
    @Getter
    private List<String> bcc;
    @Getter
    private String subject;
    @Getter
    private String body;
    @Getter
    private DataSource[] attachments;

    @Override
    public boolean send(
            final List<String> to,
            final List<String> cc,
            final List<String> bcc,
            final String subject,
            final String body,
            final DataSource... attachments) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;

        return true; // all OK
    }

    @Override
    public boolean isConfigured() {
        return true;
    }
}
