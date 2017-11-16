/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.module.application.fixtures;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.agreement.EstatioAgreementModule;
import org.estatio.module.asset.EstatioAssetModule;
import org.estatio.module.asset.dom.registration.FixedAssetRegistration;
import org.estatio.module.assetfinancial.EstatioAssetFinancialModule;
import org.estatio.module.bankaccount.EstatioBankAccountModule;
import org.estatio.module.bankmandate.EstatioBankMandateModule;
import org.estatio.module.base.EstatioBaseModule;
import org.estatio.module.budget.EstatioBudgetModule;
import org.estatio.module.budgetassignment.EstatioBudgetAssignmentModule;
import org.estatio.module.capex.EstatioCapexModule;
import org.estatio.module.event.EstatioEventModule;
import org.estatio.module.financial.EstatioFinancialModule;
import org.estatio.module.guarantee.EstatioGuaranteeModule;
import org.estatio.module.invoice.EstatioInvoiceModule;
import org.estatio.module.lease.EstatioLeaseModule;
import org.estatio.module.numerator.EstatioNumeratorModule;
import org.estatio.module.party.EstatioPartyModule;
import org.estatio.module.registration.EstatioRegistrationModule;
import org.estatio.module.registration.dom.LandRegister;

public class EstatioOperationalTeardownFixture extends TeardownFixtureAbstract {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteAllDirect(executionContext);
    }

    @Override
    protected void preDeleteFrom(final Class cls) {
        if(cls == FixedAssetRegistration.class) {
            deleteFrom(LandRegister.class);
        }
    }

    protected void deleteAllDirect(final ExecutionContext executionContext) {

        executionContext.executeChild(this, new EstatioBudgetAssignmentModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioBudgetModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioCapexModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioGuaranteeModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioLeaseModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioInvoiceModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioEventModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioBankMandateModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioAssetFinancialModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioBankAccountModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioFinancialModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioAgreementModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioRegistrationModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioAssetModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioPartyModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioNumeratorModule().getTeardownFixture());
        executionContext.executeChild(this, new EstatioBaseModule().getTeardownFixture());

    }


}
