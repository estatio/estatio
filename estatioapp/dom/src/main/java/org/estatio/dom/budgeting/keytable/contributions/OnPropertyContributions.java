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
package org.estatio.dom.budgeting.keytable.contributions;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class OnPropertyContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<KeyTable> keyTables(Property property){
        return keyTables.findByProperty(property);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "keytables", sequence = "1")
    public KeyTable newKeyTable(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate,
            final @ParameterLayout(named = "Foundation Value Type") FoundationValueType foundationValueType,
            final @ParameterLayout(named = "Key Value Method") KeyValueMethod keyValueMethod,
            final @ParameterLayout(named = "Number Of Digits") Integer numberOfDigits) {
        return keyTables.newKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
    }

    public String validateNewKeyTable(
            final Property property,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer numberOfDigits) {
        return keyTables.validateNewKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
    }

    @Inject
    private KeyTables keyTables;

}
