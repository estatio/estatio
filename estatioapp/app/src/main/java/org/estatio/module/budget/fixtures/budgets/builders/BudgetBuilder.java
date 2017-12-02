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
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.charge.dom.Charge;

import lombok.AllArgsConstructor;
import lombok.Data;
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
    List<ItemSpec> itemSpecs = Lists.newArrayList();

    @AllArgsConstructor
    @Data
    public static class ItemSpec {
        private final Charge charge;
        private final BigDecimal value;
    }

    @Getter
    Budget object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("property", executionContext, Property.class);
        checkParam("startDate", executionContext, LocalDate.class);

        Budget budget = budgetRepository.newBudget(property, startDate, startDate.plusYears(1).minusDays(1));
        for (ItemSpec itemSpec : itemSpecs) {
            budget.newBudgetItem(itemSpec.value, itemSpec.charge);
        }

        executionContext.addResult(this, startDate.toString("yyyy-MM-dd"), budget);

        object = budget;
    }

    @Inject
    BudgetRepository budgetRepository;

}
