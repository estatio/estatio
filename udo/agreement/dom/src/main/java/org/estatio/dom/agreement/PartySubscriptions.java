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
package org.estatio.dom.agreement;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PartySubscriptions extends UdoDomainService<PartySubscriptions> {

    public PartySubscriptions() {
        super(PartySubscriptions.class);
    }

    @Subscribe
    @Programmatic
    public void on(final Party.RemoveEvent ev) {
        Party sourceParty = (Party) ev.getSource();
        Party replacementParty = ev.getReplacement();

        List<AgreementRole> agreementRoles;
        switch (ev.getEventPhase()) {
        case VALIDATE:
            agreementRoles = agreementRoleRepository.findByParty(sourceParty);

            if (replacementParty == null && agreementRoles.size() > 0) {
                ev.invalidate("Party is being used in an agreement role: remove roles or provide a replacement");
            } else {
                scratchpad.put(onPartyRemoveScratchpadKey = UUID.randomUUID(), agreementRoles);
            }
            break;
        case EXECUTING:
            agreementRoles = (List<AgreementRole>) scratchpad.get(onPartyRemoveScratchpadKey);
            for (AgreementRole agreementRole : agreementRoles) {
                agreementRole.setParty(replacementParty);
            }
            break;
        default:
            break;
        }
    }


    private transient UUID onPartyRemoveScratchpadKey;

    @Inject
    private Scratchpad scratchpad;

    @Inject
    private AgreementRoleRepository agreementRoleRepository;

}