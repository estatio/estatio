package org.estatio.integtests.fakes;

import java.util.List;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.email.EmailService;

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

    private List<String> to;
    @Programmatic
    public List<String> getTo() {
        return to;
    }

    private List<String> cc;
    @Programmatic
    public List<String> getCc() {
        return cc;
    }

    private List<String> bcc;
    @Programmatic
    public List<String> getBcc() {
        return bcc;
    }

    private String subject;
    @Programmatic
    public String getSubject() {
        return subject;
    }

    private String body;
    @Programmatic
    public String getBody() {
        return body;
    }

    private DataSource[] attachments;
    @Programmatic
    public DataSource[] getAttachments() {
        return attachments;
    }

    @Programmatic
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

    @Programmatic
    @Override
    public boolean isConfigured() {
        return true;
    }
}
