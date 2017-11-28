/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
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

package org.estatio.module.budget.fixtures.budgets.personas;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;

public class BudgetForOxf2015 extends FixtureScript {

    public static final Budget_enum data = Budget_enum.OxfBudget2015;

    public static final LocalDate START_DATE = data.getStartDate();
    public static final String PROPERTY_REF = data.getProperty().getRef();

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());

        // exec
        Property property = propertyRepository.findPropertyByReference(PROPERTY_REF);
        Charge charge1 = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_1);
        Charge charge2 = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_2);

        createBudget(property, START_DATE, charge1, new BigDecimal("30000.55"), charge2, new BigDecimal("40000.35"), executionContext);

    }
    
    private Budget createBudget(final Property property, final LocalDate startDate, final Charge charge1, final BigDecimal value1, final Charge charge2, final BigDecimal value2, final ExecutionContext fixtureResults){
        Budget budget = budgetRepository.newBudget(property, startDate, startDate.plusYears(1).minusDays(1));
        budget.newBudgetItem(value1, charge1);
        budget.newBudgetItem(value2, charge2);
        return fixtureResults.addResult(this, budget);
    }

    @Inject
    private BudgetRepository budgetRepository;
    @Inject
    private ChargeRepository chargeRepository;
    @Inject
    private PropertyRepository propertyRepository;
    @Inject
    private CountryRepository countryRepository;

}
