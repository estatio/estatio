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
package org.estatio.dom.budgeting.budgetitem.contributed;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItems;
import org.estatio.dom.charge.Charge;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout()
public class BudgetContributions {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "items", sequence = "1")
    public BudgetItem newBudgetItem(
            final Budget budget,
            final BigDecimal budgetedValue,
            final Charge charge) {
        return budgetItems.newBudgetItem(budget, budgetedValue, charge);
    }

    public String validateNewBudgetItem(
            final Budget budget,
            final BigDecimal budgetedValue,
            final Charge charge) {
        return budgetItems.validateNewBudgetItem(budget,budgetedValue,charge);
    }

    @Inject
    private BudgetItems budgetItems;

}
