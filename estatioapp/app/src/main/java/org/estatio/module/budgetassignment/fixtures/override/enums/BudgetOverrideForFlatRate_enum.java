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

package org.estatio.module.budgetassignment.fixtures.override.enums;

import java.math.BigDecimal;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;

import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForFlatRate;
import org.estatio.module.budgetassignment.fixtures.override.builders.BudgetOverrideForFlatRateBuilder;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.budget.fixtures.budgets.enums.Budget_enum.BudBudget2015;
import static org.estatio.module.charge.fixtures.charges.enums.Charge_enum.NlServiceCharge;
import static org.estatio.module.lease.fixtures.lease.enums.Lease_enum.BudMiracle002Nl;
import static org.incode.module.base.integtests.VT.bd;


@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum BudgetOverrideForFlatRate_enum implements PersonaWithBuilderScript<BudgetOverrideForFlatRate,BudgetOverrideForFlatRateBuilder> {

    BudMiracle002Nl_2015(
            BudMiracle002Nl, BudBudget2015, NlServiceCharge, bd("12.5"), bd("90"), null
    );

    private final Lease_enum lease_d;
    private final Budget_enum budget_d;
    private final Charge_enum invoiceCharge_d;

    private final BigDecimal valuePerM2;
    private final BigDecimal weightedArea;

    private final Charge_enum incomingCharge_d;

    @Override
    public BudgetOverrideForFlatRateBuilder toBuilderScript() {
        return new BudgetOverrideForFlatRateBuilder()
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setPrereq((f,ec) -> f.setStartDate(f.objectFor(budget_d, ec).getStartDate()))
                .setPrereq((f,ec) -> f.setInvoiceCharge(f.objectFor(invoiceCharge_d, ec)))
                .setPrereq((f,ec) -> f.setIncomingCharge(f.objectFor(incomingCharge_d, ec)))
                .setValuePerM2(valuePerM2)
                .setWeightedArea(weightedArea)
                ;
    }

}
