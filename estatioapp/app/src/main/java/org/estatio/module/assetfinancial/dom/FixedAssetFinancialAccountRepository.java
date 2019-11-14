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
package org.estatio.module.assetfinancial.dom;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.apache.isis.applib.query.QueryDefault;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.financial.dom.FinancialAccount;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = FixedAssetFinancialAccount.class
)
public class FixedAssetFinancialAccountRepository extends UdoDomainRepositoryAndFactory<FixedAssetFinancialAccount> {

    public FixedAssetFinancialAccountRepository() {
        super(FixedAssetFinancialAccountRepository.class, FixedAssetFinancialAccount.class);
    }

    public FixedAssetFinancialAccount newFixedAssetFinancialAccount(
            final FixedAsset fixedAsset,
            final FinancialAccount financialAccount) {
        FixedAssetFinancialAccount instance = factoryService.instantiate(FixedAssetFinancialAccount.class);
        instance.setFinancialAccount(financialAccount);
        instance.setFixedAsset(fixedAsset);
        repositoryService.persistAndFlush(instance);
        return instance;
    }

    public List<FixedAssetFinancialAccount> findByFixedAsset(
            final FixedAsset fixedAsset) {
        return repositoryService.allMatches(new QueryDefault<>(FixedAssetFinancialAccount.class,"findByFixedAsset",
                "fixedAsset", fixedAsset));
    }

    public List<FixedAssetFinancialAccount> findByFinancialAccount(
            final FinancialAccount financialAccount) {
        return repositoryService.allMatches(new QueryDefault<>(FixedAssetFinancialAccount.class,"findByFinancialAccount",
                "financialAccount", financialAccount));
    }

    public FixedAssetFinancialAccount find(
            final FixedAsset fixedAsset,
            final FinancialAccount financialAccount) {
        List<FixedAssetFinancialAccount> list = repositoryService.allMatches(new QueryDefault<>(FixedAssetFinancialAccount.class,
                "findByFixedAssetAndFinancialAccount", "fixedAsset", fixedAsset,
                "financialAccount", financialAccount));
        return list.isEmpty() ? null : list.get(0);
    }

    public FixedAssetFinancialAccount findOrCreate(
            final FixedAsset fixedAsset,
            final FinancialAccount financialAccount) {
        final FixedAssetFinancialAccount instance = find(fixedAsset, financialAccount);
        if (instance == null) {
            return newFixedAssetFinancialAccount(fixedAsset, financialAccount);
        }
        return instance;
    }


}
