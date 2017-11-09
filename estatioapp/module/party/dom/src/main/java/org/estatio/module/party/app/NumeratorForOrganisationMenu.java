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

package org.estatio.module.party.app;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.party.dom.PartyConstants;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.numerator.NumeratorForOrganisationMenu"
)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "900.16"
)
public class NumeratorForOrganisationMenu extends UdoDomainRepositoryAndFactory<Numerator> {

    public NumeratorForOrganisationMenu() {
        super(NumeratorForOrganisationMenu.class, Numerator.class);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public Numerator findOrganisationReferenceNumerator(final ApplicationTenancy applicationTenancy) {
        return numeratorRepository
                .findGlobalNumerator(PartyConstants.ORGANISATION_REFERENCE_NUMERATOR_NAME, applicationTenancy);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "2")
    public Numerator createOrganisationReferenceNumerator(
            final String format,
            final BigInteger lastValue,
            final ApplicationTenancy applicationTenancy) {

        return numeratorRepository
                .createGlobalNumerator(PartyConstants.ORGANISATION_REFERENCE_NUMERATOR_NAME, format, lastValue, applicationTenancy);
    }

    public BigInteger default1CreateOrganisationReferenceNumerator() {
        return BigInteger.ZERO;
    }

    @Inject
    private NumeratorRepository numeratorRepository;

}
