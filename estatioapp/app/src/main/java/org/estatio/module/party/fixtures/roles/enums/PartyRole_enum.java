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
package org.estatio.module.party.fixtures.roles.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.party.fixtures.roles.builders.PartyRoleBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum PartyRole_enum
        implements PersonaWithBuilderScript<PartyRole, PartyRoleBuilder>,
        PersonaWithFinder<PartyRole> {

    HelloWorldIt_as_ECP(Organisation_enum.HelloWorldIt, IncomingInvoiceRoleTypeEnum.ECP),
    TopModelIt_as_SUPPLIER(Organisation_enum.TopModelIt, IncomingInvoiceRoleTypeEnum.SUPPLIER)
    ;

    private final Organisation_enum organisation_d;
    private final IPartyRoleType partyRoleType;

    @Override
    public PartyRoleBuilder builder() {
        return new PartyRoleBuilder()
                .setPrereq((f,ec) -> f.setOrganisation(f.objectFor(organisation_d,ec)))
                .setPartyRoleType(partyRoleType);
    }

    @Override
    public PartyRole findUsing(final ServiceRegistry2 serviceRegistry) {
        final Organisation organisation = organisation_d.findUsing(serviceRegistry);
        final PartyRoleTypeRepository partyRoleTypeRepository =
                serviceRegistry.lookupService(PartyRoleTypeRepository.class);
        final PartyRoleType roleType = partyRoleType.findUsing(partyRoleTypeRepository);

        return organisation.getPartyRole(roleType);
    }

}
