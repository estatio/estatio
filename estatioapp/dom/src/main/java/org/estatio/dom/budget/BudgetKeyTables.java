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
package org.estatio.dom.budget;

import java.util.List;

import com.google.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Property;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(repositoryFor = BudgetKeyTable.class)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetKeyTables extends UdoDomainRepositoryAndFactory<BudgetKeyTable> {

    public BudgetKeyTables() {
        super(BudgetKeyTables.class, BudgetKeyTable.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public BudgetKeyTable newBudgetKeyTable(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate,
            final @ParameterLayout(named = "Foundation Value Type") BudgetFoundationValueType foundationValueType,
            final @ParameterLayout(named = "Key Value Method") BudgetKeyValueMethod keyValueMethod) {
        BudgetKeyTable budgetKeyTable = newTransientInstance();
        budgetKeyTable.setProperty(property);
        budgetKeyTable.setName(name);
        budgetKeyTable.setStartDate(startDate);
        budgetKeyTable.setEndDate(endDate);
        budgetKeyTable.setFoundationValueType(foundationValueType);
        budgetKeyTable.setKeyValueMethod(keyValueMethod);
        persistIfNotAlready(budgetKeyTable);

        return budgetKeyTable;
    }

    public String validateNewBudgetKeyTable(
            final Property property,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final BudgetFoundationValueType foundationValueType,
            final BudgetKeyValueMethod keyValueMethod) {
        if (!new LocalDateInterval(startDate, endDate).isValid()) {
            return "End date can not be before start date";
        }

        return null;
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<BudgetKeyTable> allBudgetKeyTables() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<BudgetKeyTable> findBudgetKeyTableByProperty(Property property) {
        return allMatches("findByProperty", "property", property);
    }

    // //////////////////////////////////////

    @Programmatic
    public BudgetKeyTable findBudgetKeyTableByName(final String name) {
        return firstMatch("findBudgetKeyTableByName", "name", name);
    }

    // //////////////////////////////////////

    @ActionLayout(hidden = Where.EVERYWHERE)
    public List<BudgetKeyTable> autoComplete(final String search) {
        return allMatches("findBudgetKeyTableByNameMatches", "name", search.toLowerCase());
    }

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
