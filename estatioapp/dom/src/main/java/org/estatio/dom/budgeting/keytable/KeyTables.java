/*
 * Copyright 2012-2015 Eurocommercial Properties NV
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.dom.budgeting.keytable;

import com.google.inject.Inject;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Property;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;

import java.util.List;

@DomainService(repositoryFor = KeyTable.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class KeyTables extends UdoDomainRepositoryAndFactory<KeyTable> {

    public KeyTables() {
        super(KeyTables.class, KeyTable.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public KeyTable newKeyTable(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate,
            final @ParameterLayout(named = "Foundation Value Type") FoundationValueType foundationValueType,
            final @ParameterLayout(named = "Key Value Method") KeyValueMethod keyValueMethod,
            final @ParameterLayout(named = "Number Of Digits") Integer numberOfDigits) {
        KeyTable keyTable = newTransientInstance();
        keyTable.setProperty(property);
        keyTable.setName(name);
        keyTable.setStartDate(startDate);
        keyTable.setEndDate(endDate);
        keyTable.setFoundationValueType(foundationValueType);
        keyTable.setKeyValueMethod(keyValueMethod);
        keyTable.setNumberOfDigits(numberOfDigits);
        persistIfNotAlready(keyTable);

        return keyTable;
    }

    public String validateNewKeyTable(
            final Property property,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer numberOfDigits) {
        if (!new LocalDateInterval(startDate, endDate.minusDays(1)).isValid()) {
            return "End date can not be before start date";
        }
        if (findByPropertyAndNameAndStartDate(property, name, startDate)!=null) {
            return "There is already a keytable with this name for this property and startdate";
        }

        return null;
    }

    @Programmatic
    public KeyTable findOrCreateBudgetKeyTable(
            final Property property,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer numberOfDigits
    ) {
        final KeyTable keyTable = findByPropertyAndNameAndStartDate(property, name, startDate);
        if (keyTable !=null) {
            return keyTable;
        } else {
            return newKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
        }
    }


    @Programmatic
    public List<KeyTable> allKeyTables() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<KeyTable> findByProperty(Property property) {
        return allMatches("findByProperty", "property", property);
    }

    // //////////////////////////////////////

    @Programmatic
    public KeyTable findByPropertyAndNameAndStartDate(final Property property, final String name, LocalDate startDate) {
        return uniqueMatch("findByPropertyAndNameAndStartDate", "property", property, "name", name, "startDate", startDate);
    }

    // //////////////////////////////////////

    @ActionLayout(hidden = Where.EVERYWHERE)
    public List<KeyTable> autoComplete(final String search) {
        return allMatches("findKeyTableByNameMatches", "name", search.toLowerCase());
    }

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
