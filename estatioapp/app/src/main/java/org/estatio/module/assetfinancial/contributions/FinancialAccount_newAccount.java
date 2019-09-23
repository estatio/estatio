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

package org.estatio.module.assetfinancial.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.financial.dom.FinancialAccount;

@Mixin(method="act")
public class FinancialAccount_newAccount {

    private final FinancialAccount financialAccount;

    public FinancialAccount_newAccount(final FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(
            name = "Accounts",
            sequence = "13"
    )
    public FixedAssetFinancialAccount act(
            final FixedAsset fixedAsset) {
        return fixedAssetFinancialAccountContributions.newAccount(fixedAsset, this.financialAccount);
    }


    @Inject
    FixedAssetFinancialAccountContributions fixedAssetFinancialAccountContributions;

}
