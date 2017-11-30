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
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;

import static org.incode.module.base.integtests.VT.bd;

public class BudgetForOxf2016 extends FixtureScript {

    public static final Budget_enum data = Budget_enum.OxfBudget2016;


    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.toFixtureScript());

        // exec

        createBudget(
                propertyRepository.findPropertyByReference(Budget_enum.OxfBudget2016.getProperty_d().getRef()),
                Budget_enum.OxfBudget2016.getStartDate(),
                chargeRepository.findByReference(Charge_enum.GbIncomingCharge1.getRef()),
                bd("30500.99"),
                chargeRepository.findByReference(Charge_enum.GbIncomingCharge2.getRef()),
                bd("40600.01"),
                executionContext);

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
