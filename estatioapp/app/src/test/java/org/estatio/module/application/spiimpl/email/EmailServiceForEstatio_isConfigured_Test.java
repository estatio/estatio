package org.estatio.module.application.spiimpl.email;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailServiceForEstatio_isConfigured_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IsisConfiguration mockConfiguration;

    EmailServiceForEstatio esfe;

    @Before
    public void setUp() throws Exception {
        esfe = new EmailServiceForEstatio();
        esfe.configuration = mockConfiguration;
    }

    @Test
    public void when_hostname_is_null() throws Exception {

        // expect
        allowingConfigurationToReturn(null);

        // when
        final boolean configured = esfe.isConfigured();

        // then
        assertThat(configured).isFalse();
    }


    @Test
    public void when_hostname_is_empty() throws Exception {

        // expect
        allowingConfigurationToReturn("");

        // when
        final boolean configured = esfe.isConfigured();

        // then
        assertThat(configured).isFalse();
    }

    @Test
    public void when_hostname_is_not_empty() throws Exception {

        // expect
        allowingConfigurationToReturn("some.smtp.server");

        // when
        final boolean configured = esfe.isConfigured();

        // then
        assertThat(configured).isTrue();
    }

    private void allowingConfigurationToReturn(final String result) {
        context.checking(new Expectations() {{
            allowing(mockConfiguration).getString("isis.service.email.sender.hostname");
            will(returnValue(result));
        }});
    }


    @Mock MeService meService;
    @Mock PersonRepository personRepository;
    @Mock CommunicationChannelRepository communicationChannelRepository;
    @Mock EmailServiceThrowingException emailServiceThrowingException;

    @Test
    public void sendToCurrentUser_works_when_person_found() throws Exception {

        // given
        esfe.meService = meService;
        esfe.personRepository = personRepository;
        esfe.communicationChannelRepository = communicationChannelRepository;
        esfe.delegate = emailServiceThrowingException;

        final ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername("Some username");
        final Person meAsPerson = new Person();
        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("some email address");

        // expect
        context.checking(new Expectations(){{
            oneOf(meService).me();
            will(returnValue(applicationUser));
            oneOf(personRepository).findByUsername(applicationUser.getUsername());
            will(returnValue(meAsPerson));
            oneOf(communicationChannelRepository).findByOwnerAndType(meAsPerson, CommunicationChannelType.EMAIL_ADDRESS);
            will(returnValue(new TreeSet<EmailAddress>(Arrays.asList(emailAddress))));
            oneOf(emailServiceThrowingException).send(Collections.singletonList(emailAddress.getEmailAddress()), Collections.emptyList(), Collections.emptyList(), "Subject", "Message");
        }});

        // when
        esfe.sendToCurrentUser("Subject", "Message");

    }

    @Test
    public void sendToCurrentUser_works_when_no_person_found() throws Exception {

        // given
        esfe.meService = meService;
        esfe.personRepository = personRepository;
        esfe.communicationChannelRepository = communicationChannelRepository;
        esfe.delegate = emailServiceThrowingException;

        final ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername("Some username");
        applicationUser.setEmailAddress("Some email address");

        // expect
        context.checking(new Expectations(){{
            exactly(2).of(meService).me();
            will(returnValue(applicationUser));
            oneOf(personRepository).findByUsername(applicationUser.getUsername());
            will(returnValue(null));
            oneOf(emailServiceThrowingException).send(Collections.singletonList(applicationUser.getEmailAddress()), Collections.emptyList(), Collections.emptyList(), "Subject", "Message");
        }});

        // when
        esfe.sendToCurrentUser("Subject", "Message");

    }


}