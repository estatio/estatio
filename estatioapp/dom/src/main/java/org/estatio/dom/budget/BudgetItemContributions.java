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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.app.budget.BudgetCalculationServices;
import org.estatio.dom.lease.Leases;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetItemContributions {

//    @CollectionLayout(render = RenderType.EAGERLY)
//    @Action(semantics = SemanticsOf.SAFE)
//    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
//    public List<BudgetItemCalculatedValueLine> lines(final BudgetItem budgetItem) {
//
//        List<BudgetItemCalculatedValueLine> lines = new ArrayList<BudgetItemCalculatedValueLine>();
//        BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
//        LeaseTermForServiceCharge leaseTermForServiceCharge= new LeaseTermForServiceCharge();
//        String status = new String();
//
//        for (Iterator<BudgetKeyItem> it = budgetItem.getBudgetKeyTable().getBudgetKeyItems().iterator(); it.hasNext();){
//
//            budgetKeyItem = it.next();
//
//            List<Lease> leasesOnProperty = leases.findLeasesByProperty(budgetItem.getBudget().getProperty());
//            leaseTermForServiceCharge = null;
//            status="Value could not be assigned to lease term!";
//            //find term for the unit on budgetKeyItem
//            for (Lease l : leasesOnProperty) {
//                for (Occupancy o : l.getOccupancies()) {
//
//                    if (budgetKeyItem.getUnit().equals(o.getUnit())) {
//                        LeaseItem firstLeaseItemOfTypeAndCharge = o.getLease().findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE, budgetItem.getCharge());
//                        if (firstLeaseItemOfTypeAndCharge !=null) {
//                            leaseTermForServiceCharge = (LeaseTermForServiceCharge) firstLeaseItemOfTypeAndCharge.currentTerm(budgetItem.getBudget().getStartDate());
//                            status = "OK";
//                        }
//                    }
//
//                }
//            }
//            BigDecimal calculatedValue = BigDecimal.ZERO;
////            calculatedValue = calculatedValue.add(budgetItem.getValue().multiply(budgetKeyItem.getValue()).divide(new BigDecimal(1000)));
//            calculatedValue = calculatedValue.add(budgetCalculationServices.calculatedValuePerBudgetKeyItem(budgetItem,budgetKeyItem));
//            BudgetItemCalculatedValueLine newLine = new BudgetItemCalculatedValueLine(calculatedValue, leaseTermForServiceCharge, status, budgetKeyItem.getUnit(), budgetKeyItem.getValue().setScale(budgetKeyItem.getBudgetKeyTable().getNumberOfDigits(), BigDecimal.ROUND_HALF_UP));
//            lines.add(newLine);
//        }
//
//        return lines;
//
//    }

//    @Action(semantics = SemanticsOf.SAFE)
//    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
//    public BigDecimal checkTotalValue(final BudgetItem budgetItem){
//        BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
//        BigDecimal calculatedValue = new BigDecimal(0);
//        for (Iterator<BudgetKeyItem> it = budgetItem.getBudgetKeyTable().getBudgetKeyItems().iterator(); it.hasNext();){
//            budgetKeyItem = it.next();
////            calculatedValue=calculatedValue.add(budgetItem.getValue().multiply(budgetKeyItem.getValue()).divide(new BigDecimal(1000)));
//            calculatedValue = calculatedValue.add(budgetCalculationServices.calculatedValuePerBudgetKeyItem(budgetItem,budgetKeyItem));
//        }
//
//        return calculatedValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
//
//    }

    @Inject
    private Leases leases;

    @Inject
    private BudgetCalculationServices budgetCalculationServices;

}
