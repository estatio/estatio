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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class FixedAssetFinancialAccountService extends UdoDomainService<FixedAssetFinancialAccountService> {

    public FixedAssetFinancialAccountService() {
        super(FixedAssetFinancialAccountService.class);
    }

    public FixedAssetFinancialAccount newAccount(
            final FixedAsset fixedAsset,
            final FinancialAccount financialAccount) {
        return fixedAssetFinancialAccountRepository.newFixedAssetFinancialAccount(fixedAsset, financialAccount);
    }

    public List<FinancialAccount> choices1NewAccount(
            final FixedAsset fixedAsset,
            final FinancialAccount financialAccount) {
        final List<FixedAssetRole> roles = fixedAssetRoleRepository.findByAssetAndType(fixedAsset, FixedAssetRoleTypeEnum.PROPERTY_OWNER);
        List<FinancialAccount> result = new ArrayList<>();
        roles.forEach(r->{
            result.addAll(financialAccountRepository.findAccountsByOwner(r.getParty()));
        });
        return result;
    }

    // //////////////////////////////////////

    public List<FixedAssetFinancialAccount> accounts(final FixedAsset fixedAsset) {
        return fixedAssetFinancialAccountRepository.findByFixedAsset(fixedAsset);
    }

    // //////////////////////////////////////

    public List<FixedAssetFinancialAccount> fixedAssets(final FinancialAccount financialAccount) {
        return fixedAssetFinancialAccountRepository.findByFinancialAccount(financialAccount);
    }

    // //////////////////////////////////////

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

    @Inject
    FinancialAccountRepository financialAccountRepository;

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

}
