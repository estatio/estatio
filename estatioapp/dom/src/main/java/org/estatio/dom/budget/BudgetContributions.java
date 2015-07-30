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

import java.util.ArrayList;
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

import org.estatio.app.budget.BudgetCalculatedValueOnLeaseTermLine;
import org.estatio.app.budget.BudgetCalculationServices;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.Leases;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout()
public class BudgetContributions {

    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.OBJECT_ONLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<Budget> budgets(Property property){
        return budgets.findByProperty(property);
    };

    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.OBJECT_ONLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculatedValueOnLeaseTermLine> calculatedServiceChargesOnLease(final Budget budget){

        List<BudgetCalculatedValueOnLeaseTermLine> lines = new ArrayList<BudgetCalculatedValueOnLeaseTermLine>();

        List<Lease> leasesOnProperty = leases.findLeasesByProperty(budget.getProperty());
        for (Lease lease : leasesOnProperty) {

            lines.add(new BudgetCalculatedValueOnLeaseTermLine(
                    budgetCalculationServices.calculateValueOnCurrentLeaseTermForServiceCharge(lease, budget),
                    lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE).currentTerm(budget.getStartDate()))
            );

        }

        return lines;
    }


    @Inject
    Budgets budgets;

    @Inject
    Leases leases;

    @Inject
    BudgetCalculationServices budgetCalculationServices;

}
