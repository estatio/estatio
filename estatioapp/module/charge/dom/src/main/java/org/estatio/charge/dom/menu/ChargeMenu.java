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
package org.estatio.charge.dom.menu;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.dom.charge.Applicability;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.tax.Tax;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.charge.ChargeMenu"
)
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
                    regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION
            )
            final String reference,
            final String name,
            final String description,
            @Nullable
            final Tax tax,
            @Nullable
            final ChargeGroup chargeGroup,
            final Applicability applicability) {

        return chargeRepository.upsert(reference, name, description, applicationTenancy, applicability,
                tax, chargeGroup);
    }

    public String validateNewCharge(
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name,
            final String description,
            final Tax tax,
            final ChargeGroup chargeGroup,
            final Applicability applicability) {
        if(applicability.supportsOutgoing()) {
            if(tax == null) {
                return "Charges used for outgoing must have a tax";
            }
            if(chargeGroup == null) {
                return "Charges used for outgoing must have a charge group";
            }
        }
        return null;
    }

    public List<ApplicationTenancy> choices0NewCharge() {
        return allCountryTenancies();
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Charge> allCharges() {
        return chargeRepository.listAll();
    }


    // //////////////////////////////////////



    public List<ApplicationTenancy> allCountryTenancies() {
        return Lists.newArrayList(
                Iterables.filter(
                        applicationTenancies.allTenancies(), Predicates.isCountry()));
    }

    public static class Predicates {
        private Predicates() {
        }

        public static Predicate<ApplicationTenancy> isCountry() {
            return candidate -> ApplicationTenancyLevel.of(candidate).isCountry();
        }
    }


    // //////////////////////////////////////


    @Inject
    ChargeRepository chargeRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancies;


}
