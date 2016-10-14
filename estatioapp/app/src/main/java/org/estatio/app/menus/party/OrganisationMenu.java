/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.app.menus.party;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.OrganisationRepository;

@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.2")
@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
public class OrganisationMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Organisation newOrganisation(
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION, optionality = Optionality.OPTIONAL) String reference,
            final boolean useNumereratorForReference,
            final String name,
            final ApplicationTenancy applicationTenancy) {
        return organisationRepository.newOrganisation(reference, useNumereratorForReference, name, applicationTenancy);
    }

    public List<ApplicationTenancy> choices3NewOrganisation() {
        return organisationRepository.choices3NewOrganisation();
    }

    public ApplicationTenancy default3NewOrganisation() {
        return organisationRepository.default3NewOrganisation();
    }

    public String validateNewOrganisation(
            final String reference,
            final boolean useNumereratorForReference,
            final String name,
            final ApplicationTenancy applicationTenancy
    ) {
        return organisationRepository.validateNewOrganisation(reference, useNumereratorForReference, name, applicationTenancy);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<Organisation> allOrganisations() {
        return organisationRepository.allOrganisations();
    }

    // //////////////////////////////////////

    @Inject
    OrganisationRepository organisationRepository;

}
