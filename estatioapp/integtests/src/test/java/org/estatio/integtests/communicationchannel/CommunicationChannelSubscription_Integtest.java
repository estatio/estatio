package org.estatio.integtests.communicationchannel;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationRepository;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

/*
TODO: This integegration test needs to be moved to incode-module-communications

We failed for a number of reasons:
1. There is no driver for HSQLDB when extending the AbstractIntegTest in the module
2. There is no communication channel owner in the fixture nor has the module a dependency on party.

Separately there is no convenience superclasss which registers the service on the event bus

*/


public class CommunicationChannelSubscription_Integtest extends EstatioIntegrationTest {

    @Inject CommunicationChannelRepository communicationChannelRepository;

    @Inject CommunicationRepository communicationRepository;

    @Inject PartyRepository partyRepository;

    @Before
    public void setUp() throws Exception {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(FixtureScript.ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new OrganisationForHelloWorldNl());
            }
        });
    }

    @Test
    public void replacement_provided() throws Exception {
        // Given
        final Party owner = partyRepository.findPartyByReference(OrganisationForHelloWorldNl.REF);
        final PostalAddress postalAddress = communicationChannelRepository.newPostal(
                owner,
                CommunicationChannelType.POSTAL_ADDRESS,
                "Some address",
                null,
                null,
                null,
                null,
                null,
                null);

        final PostalAddress replaceWith = communicationChannelRepository.newPostal(
                owner,
                CommunicationChannelType.POSTAL_ADDRESS,
                "Replacement Address",
                null,
                null,
                null,
                null,
                null,
                null);

        final Communication communication = communicationRepository.createPostal("Subject", "/", postalAddress);

        assertThat(communicationRepository.findByCommunicationChannel(postalAddress).size()).isEqualTo(1);

        //            // Expect
        //            expectedExceptions.expectMessage(containsString("Communication channel is being used (as the 'sendTo' channel for 1 invoice(s); provide a replacement"));

        // When
        wrap(postalAddress).remove(replaceWith);

    }

    @Test
    public void validate_fails_when_no_replacement_is_provided() throws Exception {
        // Given
        final Party owner = partyRepository.findPartyByReference(OrganisationForHelloWorldNl.REF);
        final PostalAddress postalAddress = communicationChannelRepository.newPostal(
                owner,
                CommunicationChannelType.POSTAL_ADDRESS,
                "Some address",
                null,
                null,
                null,
                null,
                null,
                null);


        final Communication communication = communicationRepository.createPostal("Subject", "/", postalAddress);

        // Expect
        expectedExceptions.expectMessage("Communication channel is being used");

        // When
        wrap(postalAddress).remove(null);

    }

}
