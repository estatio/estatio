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
package org.estatio.module.assetfinancial.integtests.financial;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;

import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccountRepository;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccountFaFa_enum;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccount_enum;
import org.estatio.module.assetfinancial.integtests.AssetFinancialModuleIntegTestAbstract;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;

public class FinancialAccount_IntegTest extends AssetFinancialModuleIntegTestAbstract {

    public static class GetOwner extends FinancialAccount_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, BankAccount_enum.HelloWorldGb.builder());
                    executionContext.executeChild(this, BankAccountFaFa_enum.HelloWorldGb.builder());
                }
            });
        }

        @Inject
        private PartyRepository partyRepository;
        @Inject
        private FinancialAccountRepository financialAccountRepository;

        private Party party;

        @Before
        public void setUp() throws Exception {
            party = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
        }

        // this test really just makes an assertion about the fixture.
        @Test
        public void atLeastOneAccountIsOwnedByParty() throws Exception {

            // given
            List<FinancialAccount> allAccounts = financialAccountRepository.allAccounts();

            // when
            List<FinancialAccount> partyAccounts = Lists.newArrayList(Iterables.filter(allAccounts, new Predicate<FinancialAccount>() {
                public boolean apply(FinancialAccount fa) {
                    return fa.getOwner() == party;
                }
            }));

            // then
            Assert.assertThat(partyAccounts.size(), is(1));
        }

    }

    public static class RemoveBankAccount extends FinancialAccount_IntegTest {

        @Inject
        private FinancialAccountRepository financialAccountRepository;

        @Inject
        private FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

        @Inject
        private PartyRepository partyRepository;

        @Inject
        private SudoService sudoService;

        private BankAccount bankAccount;

        private FixedAssetFinancialAccount fixedAssetFinancialAccount;

        private Party owner;

        @Before
        public void setUp() throws Exception {

            runFixtureScript(
                    BankAccountFaFa_enum.HelloWorldNl.builder(),
                    OrganisationAndComms_enum.HelloWorldNl.builder()
            );

            bankAccount = BankAccountFaFa_enum.HelloWorldNl.getBankAccount_d().findUsing(serviceRegistry);
            owner = OrganisationAndComms_enum.HelloWorldNl.findUsing(serviceRegistry);
        }

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void happyCase() throws Exception {
            // Given
            List<FixedAssetFinancialAccount> results = fixedAssetFinancialAccountRepository.findByFinancialAccount(bankAccount);
            Assert.assertThat(results.size(), is(1));
            fixedAssetFinancialAccount = results.get(0);

            Assert.assertThat(fixedAssetFinancialAccount.getFixedAsset().getReference(), is(
                    Property_enum.KalNl.getRef()));

            // When
            sudoService.sudo("estatio-admin", Lists.newArrayList(EstatioRole.ADMINISTRATOR.getRoleName()),
                    () -> {
                        wrap(fixedAssetFinancialAccount).remove();
                        Assert.assertThat(fixedAssetFinancialAccountRepository.findByFinancialAccount(bankAccount).size(), is(0));
                        wrap(bankAccount).remove("Some reason");
                    });

            // Then
            Assert.assertThat(fixedAssetFinancialAccountRepository.findByFinancialAccount(bankAccount).size(), is(0));
            Assert.assertNull(financialAccountRepository.findByOwnerAndReference(owner, BankAccountFaFa_enum.HelloWorldNl.getBankAccount_d().getIban()));

        }
    }
}