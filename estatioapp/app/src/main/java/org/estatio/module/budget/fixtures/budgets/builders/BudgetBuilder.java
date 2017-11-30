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

package org.estatio.module.budget.fixtures.budgets.builders;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@EqualsAndHashCode(of={"property", "startDate"}, callSuper = false)
@ToString(of={"property", "startDate"})
@Accessors(chain = true)
public class BudgetBuilder extends BuilderScriptAbstract<Budget, BudgetBuilder> {

    @Getter @Setter
    Property property;
    @Getter @Setter
    LocalDate startDate;
    @Getter @Setter
    Charge charge1;
    @Getter @Setter
    BigDecimal value1;
    @Getter @Setter
    Charge charge2;
    @Getter @Setter
    BigDecimal value2;

    @Getter
    Budget object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("property", executionContext, Property.class);
        checkParam("startDate", executionContext, LocalDate.class);
        checkParam("charge1", executionContext, Charge.class);
        checkParam("value1", executionContext, BigDecimal.class);
        checkParam("charge2", executionContext, Charge.class);
        checkParam("value2", executionContext, BigDecimal.class);

        Budget budget = budgetRepository.newBudget(property, startDate, startDate.plusYears(1).minusDays(1));
        budget.newBudgetItem(value1, charge1);
        budget.newBudgetItem(value2, charge2);

        executionContext.addResult(this, startDate.toString("yyyy-MM-dd"), budget);
    }

    @Inject
    BudgetRepository budgetRepository;

}
