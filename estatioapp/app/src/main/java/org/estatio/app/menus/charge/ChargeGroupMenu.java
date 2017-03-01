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
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroupRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.charge.ChargeGroupMenu"
)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.2")
public class ChargeGroupMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public List<ChargeGroup> newChargeGroup(
            final @ParameterLayout(named = "Reference") @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final @ParameterLayout(named = "Description") String description) {
        chargeGroupRepository.createChargeGroup(reference, description);
        return allChargeGroups();
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<ChargeGroup> allChargeGroups() {
        return chargeGroupRepository.allChargeGroups();
    }

    // //////////////////////////////////////

    @Inject
    private ChargeGroupRepository chargeGroupRepository;
}
