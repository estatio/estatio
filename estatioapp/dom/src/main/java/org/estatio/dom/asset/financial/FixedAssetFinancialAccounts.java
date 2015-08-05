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
package org.estatio.dom.asset.financial;

import java.util.List;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.bankaccount.BankAccount;

@DomainService(menuOrder = "10", repositoryFor = FixedAssetFinancialAccount.class)
public class FixedAssetFinancialAccounts extends UdoDomainRepositoryAndFactory<FixedAssetFinancialAccount> {

    public FixedAssetFinancialAccounts() {
        super(FixedAssetFinancialAccounts.class, FixedAssetFinancialAccount.class);
    }

    @Programmatic
    public FixedAssetFinancialAccount newFixedAssetFinancialAccount(
            final FixedAsset fixedAsset,
            final FinancialAccount financialAccount) {
        FixedAssetFinancialAccount instance = newTransientInstance(FixedAssetFinancialAccount.class);
        instance.setFinancialAccount(financialAccount);
        instance.setFixedAsset(fixedAsset);
        persistIfNotAlready(instance);
        return instance;
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FixedAssetFinancialAccount> findByFixedAsset(
            final FixedAsset fixedAsset) {
        return allMatches("findByFixedAsset",
                "fixedAsset", fixedAsset);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FixedAssetFinancialAccount> findByFinancialAccount(
            final FinancialAccount financialAccount) {
        return allMatches("findByFinancialAccount",
                "financialAccount", financialAccount);
    }

    // //////////////////////////////////////

    @Programmatic
    public FixedAssetFinancialAccount find(
            final FixedAsset fixedAsset,
            final FinancialAccount financialAccount) {
        return firstMatch("findByFixedAssetAndFinancialAccount",
                "fixedAsset", fixedAsset,
                "financialAccount", financialAccount);
    }

    @Programmatic
    public FixedAssetFinancialAccount findOrCreate(
            final FixedAsset fixedAsset,
            final FinancialAccount financialAccount) {
        final FixedAssetFinancialAccount instance = find(fixedAsset, financialAccount);
        if (instance == null) {
            return newFixedAssetFinancialAccount(fixedAsset, financialAccount);
        }
        return instance;
    }

    @Subscribe
    @Programmatic
    public void on(final BankAccount.RemoveEvent ev) {
        BankAccount sourceBankAccount = ev.getSource();

        List<FixedAssetFinancialAccount> results;
        switch (ev.getEventPhase()) {
        case VALIDATE:
            results = findByFinancialAccount(sourceBankAccount);
            if (results.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("This bank account is assigned to a fixed asset: remove the bank account from the fixed asset. In use by the following fixed assets: ");
                for (FixedAssetFinancialAccount fixedAssetFinancialAccount : results) {
                    stringBuilder.append(fixedAssetFinancialAccount.getFixedAsset().getName() + "\n");
                }
                ev.invalidate(stringBuilder.toString());
            }
            break;
        default:
            break;
        }
    }

}
