package org.estatio.module.application.spiimpl.email;

import java.util.List;

import javax.activation.DataSource;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.email.EmailService;

public interface EmailService2 extends EmailService {

    /**
     * Analogous to EmailService#send, except for that it allows the user to provide isis.properties keys for
     * a non-default email/password pair
     */
    @Programmatic
    boolean send(final List<String> to, final List<String> cc, final List<String> bcc, final String from, final String subject, final String body, final DataSource... attachments);

}
