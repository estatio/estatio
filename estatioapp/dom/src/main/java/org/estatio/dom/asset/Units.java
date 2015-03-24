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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.utils.StringUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(repositoryFor = Unit.class)
@DomainServiceLayout(
        named = "Fixed Assets",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "10.2")
public class Units extends EstatioDomainService<Unit> {

    @Inject
    ClockService clockService;

    public Units() {
        super(Units.class, Unit.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Unit newUnit(
            final Property property,
            final @Parameter(regexPattern = RegexValidation.Unit.REFERENCE) String reference,
            final String name,
            final UnitType type) {
        final Unit unit = newTransientInstance();
        unit.setReference(reference);
        unit.setName(name);
        unit.setType(type);
        unit.setProperty(property);
        persist(unit);
        return unit;
    }

    public UnitType default3NewUnit() {
        return UnitType.BOUTIQUE;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Unit> findUnits(
            final @ParameterLayout(named = "Reference or Name", describedAs = "May include wildcards '*' and '?'") String referenceOrName,
            final @ParameterLayout(named = "Include terminated") boolean includeTerminated) {
        return allMatches("findByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName),
                "includeTerminated", includeTerminated,
                "date", clockService.now());
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public Unit findUnitByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public List<Unit> findByProperty(final Property property) {
        return allMatches("findByProperty", "property", property);
    }

    @Programmatic
    public List<Unit> findByActiveOnDate(LocalDate date) {
        return allMatches("findByActiveOnDate", "startDate", date, "endDate", LocalDateInterval.endDateFromStartDate(date));
    }

    @Programmatic
    public List<Unit> findByPropertyAndActiveNow(final Property property) {
        LocalDate now = clockService.now();
        return findByPropertyAndActiveOnDate(property, now);
    }

    @Programmatic
    public List<Unit> findByPropertyAndActiveOnDate(final Property property, LocalDate date) {
        return allMatches("findByPropertyAndActiveOnDate", "property", property, "date", date);
    }

    // //////////////////////////////////////

    @Collection(hidden = Where.EVERYWHERE)
    public List<Unit> autoComplete(final String searchPhrase) {
        return findUnits("*".concat(searchPhrase).concat("*"), false);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "99")
    public List<Unit> allUnits() {
        return allInstances();
    }

}
