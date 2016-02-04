/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.dom.budgeting.budgetitem;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.charge.Charge;

import javax.inject.Inject;
import java.math.BigDecimal;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout()
public class BudgetItemContributions {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "items", sequence = "1")
    public BudgetItem newBudgetItem(
            final Budget budget,
            final BigDecimal budgetedValue,
            final Charge charge) {
        return budgetItemRepository.newBudgetItem(budget, budgetedValue, charge);
    }

    public String validateNewBudgetItem(
            final Budget budget,
            final BigDecimal budgetedValue,
            final Charge charge) {
        return budgetItemRepository.validateNewBudgetItem(budget,budgetedValue,charge);
    }

    @Inject
    private BudgetItemRepository budgetItemRepository;

}
