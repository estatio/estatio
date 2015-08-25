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
package org.estatio.dom.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.app.budget.BudgetCalculationServices;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Leases;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout()
public class BudgetContributions {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public BudgetItem newBudgetItem(
            final Budget budget,
            final BudgetKeyTable budgetKeyTable,
            final BigDecimal value,
            final Charge charge,
            final BudgetCostGroup budgetCostGroup) {
        return budgetItems.newBudgetItem(budget, budgetKeyTable, value, charge, budgetCostGroup);
    }

    public String validateNewBudgetItem(
            final Budget budget,
            final BudgetKeyTable budgetKeyTable,
            final BigDecimal value,
            final Charge charge,
            final BudgetCostGroup budgetCostGroup) {
        if (value.equals(new BigDecimal(0))) {
            return "Value can't be zero";
        }
        return null;
    }

    public List<BudgetKeyTable> choices1NewBudgetItem(
            final Budget budget,
            final BudgetKeyTable budgetKeyTable,
            final BigDecimal value,
            final Charge charge,
            final BudgetCostGroup budgetCostGroup) {

        return budgetKeyTables.findBudgetKeyTableByProperty(budget.getProperty());

    }


    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.OBJECT_ONLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<Budget> budgets(Property property) {
        return budgets.findBudgetByProperty(property);
    }


//    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.OBJECT_ONLY)
//    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
//    @CollectionLayout(render = RenderType.EAGERLY)
//    public List<BudgetCalculatedValueOnLeaseTermLine> calculatedServiceChargesOnLease(final Budget budget) {
//
//        List<BudgetCalculatedValueOnLeaseTermLine> lines = new ArrayList<BudgetCalculatedValueOnLeaseTermLine>();
//
//        List<Lease> leasesOnProperty = leases.findLeasesByProperty(budget.getProperty());
//        for (Lease lease : leasesOnProperty) {
//            LeaseItem firstLeaseItemOfType = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
//            if (firstLeaseItemOfType != null) {
//                if (firstLeaseItemOfType.currentTerm(budget.getStartDate()) != null) {
//                    lines.add(new BudgetCalculatedValueOnLeaseTermLine(
//                                    budgetCalculationServices.calculateValueOnCurrentLeaseTermForServiceCharge(lease, budget),
//                                    firstLeaseItemOfType.currentTerm(budget.getStartDate()),
//                                    firstLeaseItemOfType.getLease().getOccupancies().first().getUnit().getReference())
//                    );
//                }
//            }
//
//        }
//
//        return lines;
//    }

    @Inject
    private Budgets budgets;

    @Inject
    private Leases leases;

    @Inject
    private BudgetCalculationServices budgetCalculationServices;

    @Inject
    private BudgetItems budgetItems;

    @Inject
    private BudgetKeyTables budgetKeyTables;

}
