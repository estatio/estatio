/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
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
