/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.estatio.module.assetfinancial.integtests.assets;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.FixedAssetRepository;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccountRepository;
import org.estatio.module.assetfinancial.fixtures.enums.BankAccountFaFa_enum;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.assetfinancial.integtests.AssetFinancialModuleIntegTestAbstract;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.fixtures.security.users.personas.EstatioAdmin;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FixedAssetFinancialAccountRepository_IntegTest extends AssetFinancialModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                executionContext.executeChild(this, BankAccount_enum.Oxford.builder());
                executionContext.executeChild(this, BankAccountFaFa_enum.Oxford.builder());
            }
        });
        owner = PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.getOwner_d().findUsing(serviceRegistry);
    }

    @Inject
    FixedAssetRepository fixedAssetRepository;

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    SudoService sudoService;

    @Inject
    PartyRepository partyRepository;

    Party owner;

    public static class FindByFixedAsset extends FixedAssetFinancialAccountRepository_IntegTest {

        @Test
        public void findByFixedAsset() throws Exception {
            // given
            List<FixedAsset> fixedAsset = fixedAssetRepository.matchAssetsByReferenceOrName(
                    Property_enum.OxfGb.getRef());
            assertThat(fixedAsset.size(), is(1));

            // when
            final List<FixedAssetFinancialAccount> results = fixedAssetFinancialAccountRepository.findByFixedAsset(fixedAsset.get(0));

            // then
            assertThat(results.size(), is(1));
        }
    }

    public static class FindByFinancialAccount extends FixedAssetFinancialAccountRepository_IntegTest {

        @Test
        public void findByFinancialAccount() throws Exception {
            // given
            List<FixedAsset> fixedAsset = fixedAssetRepository.matchAssetsByReferenceOrName(
                    Property_enum.OxfGb.getRef());
            assertThat(fixedAsset.size(), is(1));

            // when
            final List<FixedAssetFinancialAccount> results = fixedAssetFinancialAccountRepository.findByFinancialAccount(bankAccountRepository.findBankAccountByReference(owner,
                    BankAccountFaFa_enum.Oxford.getBankAccount_d().getIban()));

            // then
            assertThat(results.size(), is(1));
        }
    }

    public static class Find extends FixedAssetFinancialAccountRepository_IntegTest {

        @Test
        public void find() throws Exception {
            // given
            List<FixedAsset> fixedAsset = fixedAssetRepository.matchAssetsByReferenceOrName(
                    Property_enum.OxfGb.getRef());
            assertThat(fixedAsset.size(), is(1));

            // when
            final FixedAssetFinancialAccount result = fixedAssetFinancialAccountRepository.find(fixedAsset.get(0), bankAccountRepository.findBankAccountByReference(owner,
                    BankAccountFaFa_enum.Oxford.getBankAccount_d().getIban()));

            // then
            assertNotNull(result);
        }
    }

    public static class OnBankAccountRemove extends FixedAssetFinancialAccountRepository_IntegTest {

        BankAccount oldBankAccount;
        BankAccount newBankAccount;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void setUp() throws Exception {
            oldBankAccount = bankAccountRepository.findBankAccountByReference(owner, BankAccountFaFa_enum.Oxford.getBankAccount_d().getIban());
            final Organisation organisation = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
            newBankAccount = bankAccountRepository.newBankAccount(
                    organisation, "NEWBANKACCOUNT", null);
        }

        @Test
        public void removeFixedAssetFinancialAccount() throws Exception {
            // Given
            FixedAssetFinancialAccount fixedAssetFinancialAccount = fixedAssetFinancialAccountRepository.findByFinancialAccount(oldBankAccount).get(0);

            // When
            wrap(fixedAssetFinancialAccount).remove();

            // Then
            Assert.assertThat(fixedAssetFinancialAccountRepository.findByFinancialAccount(oldBankAccount).size(), is(0));
        }

        @Test
        public void whenVetoingSubscriber() {
            // Then
            expectedException.expect(InvalidException.class);

            // WHen
            sudoService.sudo(EstatioAdmin.USER_NAME, Lists.newArrayList(EstatioRole.ADMINISTRATOR.getRoleName()),
                    () -> wrap(oldBankAccount).remove("Some reason"));
        }
    }
}
