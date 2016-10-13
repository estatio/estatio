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
package org.estatio.app.menus.charge;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.types.ReferenceType;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.tax.Tax;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.3")
public class ChargeMenu extends UdoDomainRepositoryAndFactory<Charge> {

    public ChargeMenu() {
        super(ChargeMenu.class, Charge.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "1")
    public Charge newCharge(
            final ApplicationTenancy applicationTenancy,
            @Parameter(
                    regexPattern = ReferenceType.Meta.REGEX,
                    regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION)
            @ParameterLayout(named = "Reference")
            final String reference,
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "Description")
            final String description,
            final Tax tax,
            final ChargeGroup chargeGroup) {

        return chargeRepository.newCharge(applicationTenancy, reference, name, description, tax, chargeGroup);
    }

    public List<ApplicationTenancy> choices0NewCharge() {
        return estatioApplicationTenancyRepository.allCountryTenancies();
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Charge> allCharges() {
        return chargeRepository.allCharges();
    }


    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    private ChargeRepository chargeRepository;


}
