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
package org.estatio.dom.asset;

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
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainService;

@DomainService(
        nature = NatureOfService.VIEW
)
@DomainServiceLayout(
        named = "Fixed Assets",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "10.2"
)
public class UnitMenu extends UdoDomainService<UnitMenu> {

    @Inject
    ClockService clockService;

    @Inject
    UnitRepository unitRepository;

    public UnitMenu() {
        super(UnitMenu.class);
    }

    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @MemberOrder(sequence = "1")
    public Unit newUnit(
            final Property property,
            final @Parameter(regexPattern = RegexValidation.Unit.REFERENCE, regexPatternReplacement = RegexValidation.Unit.REFERENCE_DESCRIPTION) String reference,
            final String name,
            final UnitType type) {
        return unitRepository.newUnit(property, reference, name, type);
    }

    public UnitType default3NewUnit() {
        return UnitType.BOUTIQUE;
    }

    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "2")
    public List<Unit> findUnits(
            final @ParameterLayout(named = "Reference or Name", describedAs = "May include wildcards '*' and '?'") String referenceOrName,
            final @ParameterLayout(named = "Include terminated") boolean includeTerminated) {
        return unitRepository.findUnits(referenceOrName, includeTerminated);
    }


    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "99")
    public List<Unit> allUnits() {
        return unitRepository.allUnits();
    }

}
