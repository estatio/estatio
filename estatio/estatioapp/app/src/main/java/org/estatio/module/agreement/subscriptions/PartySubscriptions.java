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
package org.estatio.module.agreement.subscriptions;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PartySubscriptions extends UdoDomainService<PartySubscriptions> {

    public PartySubscriptions() {
        super(PartySubscriptions.class);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final Party.DeleteEvent ev) {
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


    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final Party.FixEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTING:
            Party sourceParty = (Party) ev.getSource();
            final List<AgreementRole> roles = agreementRoleRepository.findByParty(sourceParty);
            for (AgreementRole role : roles) {
                sourceParty.addRole(role.getType());
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