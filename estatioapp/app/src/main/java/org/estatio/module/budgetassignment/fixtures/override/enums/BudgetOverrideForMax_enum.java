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
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForMax;
import org.estatio.module.budgetassignment.fixtures.override.builders.BudgetOverrideForMaxBuilder;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.budget.fixtures.budgets.enums.Budget_enum.BudBudget2015;
import static org.estatio.module.charge.fixtures.charges.enums.Charge_enum.NlIncomingCharge1;
import static org.estatio.module.charge.fixtures.charges.enums.Charge_enum.NlServiceCharge;
import static org.estatio.module.lease.fixtures.lease.enums.Lease_enum.BudPoison001Nl;
import static org.incode.module.base.integtests.VT.bd;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum BudgetOverrideForMax_enum implements PersonaWithBuilderScript<BudgetOverrideForMax,BudgetOverrideForMaxBuilder> {

    BudPoison001Nl_2015(
            BudPoison001Nl, BudBudget2015, NlServiceCharge, bd("350.00"), NlIncomingCharge1
    );

    private final Lease_enum lease_d;
    private final Budget_enum budget_d;
    private final Charge_enum invoiceCharge_d;

    private final BigDecimal maxValue;

    private final Charge_enum incomingCharge_d;

    @Override
    public BudgetOverrideForMaxBuilder builder() {
        return new BudgetOverrideForMaxBuilder()
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setPrereq((f,ec) -> f.setStartDate(f.objectFor(budget_d, ec).getStartDate()))
                .setPrereq((f,ec) -> f.setInvoiceCharge(f.objectFor(invoiceCharge_d, ec)))
                .setPrereq((f,ec) -> f.setIncomingCharge(f.objectFor(incomingCharge_d, ec)))
                .setMaxValue(maxValue)
                ;
    }
}
