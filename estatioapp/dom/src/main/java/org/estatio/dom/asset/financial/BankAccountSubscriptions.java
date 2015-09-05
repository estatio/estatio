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

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.financial.bankaccount.BankAccount;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class BankAccountSubscriptions extends UdoDomainService<BankAccountSubscriptions> {

    public BankAccountSubscriptions() {
        super(BankAccountSubscriptions.class);
    }


    @Subscribe
    @Programmatic
    public void on(final BankAccount.RemoveEvent ev) {
        BankAccount sourceBankAccount = ev.getSource();

        List<FixedAssetFinancialAccount> results;
        switch (ev.getEventPhase()) {
        case VALIDATE:
            results = fixedAssetFinancialAccountRepository.findByFinancialAccount(sourceBankAccount);
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

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

}
