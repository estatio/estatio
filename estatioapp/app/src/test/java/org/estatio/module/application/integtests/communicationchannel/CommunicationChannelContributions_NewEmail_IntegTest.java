package org.estatio.module.application.integtests.communicationchannel;

import java.util.SortedSet;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_newChannelContributions;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;

import org.estatio.module.application.integtests.ApplicationModuleIntegTestAbstract;
import org.estatio.module.asset.fixtures.person.builders.PersonAndRolesBuilder;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

public class CommunicationChannelContributions_NewEmail_IntegTest extends ApplicationModuleIntegTestAbstract {

    @Inject
    CommunicationChannelOwner_newChannelContributions communicationChannelContributions;

    @Inject
    PartyRepository partyRepository;

    Party party;

    private PersonAndRolesBuilder fs;

    @Before
    public void setup() {
        fs = new PersonAndRolesBuilder();

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, fs);
            }
        });
    }

    @Test
    public void happyCase() throws Exception {

        final Party party = fs.getObject();

        // given
        final SortedSet<CommunicationChannel> before = communicationChannelContributions.communicationChannels(party);
        Assertions.assertThat(before).isEmpty();

        // when
        final String emailAddress = "bar@foo.com";
        wrap(communicationChannelContributions).newEmail(party, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);

        // then
        final SortedSet<CommunicationChannel> after = communicationChannelContributions.communicationChannels(party);
        Assertions.assertThat(after).hasSize(1);

        final CommunicationChannel communicationChannel = after.first();
        Assertions.assertThat(communicationChannel).isInstanceOf(EmailAddress.class);
        Assertions.assertThat(((EmailAddress)communicationChannel).getEmailAddress()).isEqualTo(emailAddress);
    }

    @Test
    public void sadCase_wronglyFormattedEmail() throws Exception{
        final Party party = fs.getObject();

        // given
        final SortedSet<CommunicationChannel> before = communicationChannelContributions.communicationChannels(party);
        Assertions.assertThat(before).isEmpty();

        // then
        expectedExceptions.expect(InvalidException.class);
        expectedExceptions.expectMessage("This email address is invalid");

        // when
        final String emailAddress = "Badly formatted @email address .it";
        wrap(communicationChannelContributions).newEmail(party, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);

        // finally
        final SortedSet<CommunicationChannel> after = communicationChannelContributions.communicationChannels(party);
        Assertions.assertThat(after).isEmpty();
    }

}
