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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.app.budget.BudgetCalculationServices;
import org.estatio.app.budget.BudgetItemCalculatedValueLine;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancy;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetItemContributions extends UdoDomainRepositoryAndFactory<BudgetItem> {

    public BudgetItemContributions() {
        super(BudgetItemContributions.class, BudgetItem.class);
    }

    @CollectionLayout(render = RenderType.EAGERLY)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetItemCalculatedValueLine> lines(final BudgetItem budgetItem) {

        List<BudgetItemCalculatedValueLine> lines = new ArrayList<BudgetItemCalculatedValueLine>();
        BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
        LeaseTermForServiceCharge leaseTermForServiceCharge= new LeaseTermForServiceCharge();
        String status = new String();

        for (Iterator<BudgetKeyItem> it = budgetItem.getBudgetKeyTable().getBudgetKeyItems().iterator(); it.hasNext();){

            budgetKeyItem = it.next();

            List<Lease> leasesOnProperty = leases.findLeasesByProperty(budgetItem.getBudget().getProperty());
            leaseTermForServiceCharge = null;
            status="Value could not be assigned to lease term!";
            //find term for the unit on budgetKeyItem
            for (Lease l : leasesOnProperty) {
                for (Occupancy o : l.getOccupancies()) {

                    if (budgetKeyItem.getUnit().equals(o.getUnit())) {
                        //TODO: other lease Item Types depending on BudgetItem charge
                        leaseTermForServiceCharge = (LeaseTermForServiceCharge) o.getLease().findFirstItemOfType(LeaseItemType.SERVICE_CHARGE).currentTerm(budgetItem.getBudget().getStartDate());
                        status="OK";
                    }

                }
            }
            BigDecimal calculatedValue = BigDecimal.ZERO;
//            calculatedValue = calculatedValue.add(budgetItem.getValue().multiply(budgetKeyItem.getKeyValue()).divide(new BigDecimal(1000)));
            calculatedValue = calculatedValue.add(budgetCalculationServices.calculatedValuePerBudgetKeyItem(budgetItem,budgetKeyItem));
            BudgetItemCalculatedValueLine newLine = new BudgetItemCalculatedValueLine(calculatedValue, leaseTermForServiceCharge, status, budgetKeyItem.getUnit(), budgetKeyItem.getKeyValue());
            lines.add(newLine);
        }

        return lines;

    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal checkTotalValue(final BudgetItem budgetItem){
        BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
        BigDecimal calculatedValue = new BigDecimal(0);
        for (Iterator<BudgetKeyItem> it = budgetItem.getBudgetKeyTable().getBudgetKeyItems().iterator(); it.hasNext();){
            budgetKeyItem = it.next();
//            calculatedValue=calculatedValue.add(budgetItem.getValue().multiply(budgetKeyItem.getKeyValue()).divide(new BigDecimal(1000)));
            calculatedValue = calculatedValue.add(budgetCalculationServices.calculatedValuePerBudgetKeyItem(budgetItem,budgetKeyItem));
        }

        return calculatedValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);

    }

    @Inject
    Leases leases;

    @Inject
    BudgetCalculationServices budgetCalculationServices;

}
