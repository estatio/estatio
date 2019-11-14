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
package org.estatio.module.asset.dom;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.query.QueryDefault;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Unit.class
)
public class UnitRepository extends UdoDomainRepositoryAndFactory<Unit> {


    @Inject
    ClockService clockService;

    public UnitRepository() {
        super(UnitRepository.class, Unit.class);
    }

    // //////////////////////////////////////

    public Unit newUnit(
            final Property property,
            final String reference,
            final String name,
            final UnitType type) {
        final Unit unit = factoryService.instantiate(Unit.class);
        unit.setReference(reference);
        unit.setName(name);
        unit.setType(type);
        unit.setProperty(property);
        repositoryService.persistAndFlush(unit);
        return unit;
    }

    // //////////////////////////////////////

    public List<Unit> findUnits(
            final String referenceOrName,
            final boolean includeTerminated) {
        return repositoryService.allMatches(new QueryDefault<>(Unit.class,"findByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName),
                "includeTerminated", includeTerminated,
                "date", clockService.now()));
    }

    public Unit findUnitByReference(final String reference) {
        List<Unit> list = repositoryService.allMatches(new QueryDefault<>(Unit.class,
                "findByReference", "reference", reference));
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Unit> findByProperty(final Property property) {
        return repositoryService.allMatches(new QueryDefault<>(Unit.class,
                "findByProperty", "property", property));
    }

    public List<Unit> findByActiveOnDate(LocalDate date) {
        return repositoryService.allMatches(new QueryDefault<>(Unit.class,"findByActiveOnDate",
                "startDate", date, "endDate", LocalDateInterval.endDateFromStartDate(date)));
    }

    public List<Unit> findByPropertyAndActiveNow(final Property property) {
        LocalDate now = clockService.now();
        return findByPropertyAndActiveOnDate(property, now);
    }

    public List<Unit> findByPropertyAndActiveOnDate(final Property property, LocalDate date) {
        return repositoryService.allMatches(new QueryDefault<>(Unit.class,"findByPropertyAndActiveOnDate",
                "property", property, "date", date));
    }

    /**
     * Autocomplete for {@link Unit}, as per {@link DomainObject#autoCompleteRepository()}.
     */
    public List<Unit> autoComplete(final String searchPhrase) {
        return findUnits("*".concat(searchPhrase).concat("*"), false);
    }

    public List<Unit> allUnits() {
        return allInstances();
    }


}
