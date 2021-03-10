package org.estatio.dom.communicationchannel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class CommunicationChannelRepository_Test {

    FinderInteraction finderInteraction;

    CommunicationChannelRepository communicationChannelRepository;

    CommunicationChannelType type;

    @Before
    public void setup() {

        type = CommunicationChannelType.EMAIL_ADDRESS;

        communicationChannelRepository = new CommunicationChannelRepository();
    }

    public static class FindByReferenceAndType extends CommunicationChannelRepository_Test {

        @Ignore
        @Test
        public void happyCase() {

            communicationChannelRepository.findByReferenceAndType("REF-1", type);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(CommunicationChannel.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByReferenceAndType");
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference")).isEqualTo((Object) "REF-1");
            assertThat(finderInteraction.getArgumentsByParameterName().get("type")).isEqualTo((Object) type);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }
    }
}
