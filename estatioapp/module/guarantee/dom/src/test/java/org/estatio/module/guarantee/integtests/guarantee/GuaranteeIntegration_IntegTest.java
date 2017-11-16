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
package org.estatio.module.guarantee.integtests.guarantee;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.module.base.integtests.VT;

import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeRepository;
import org.estatio.module.guarantee.dom.GuaranteeType;
import org.estatio.module.guarantee.fixtures.personas.GuaranteeForOxfTopModel001Gb;
import org.estatio.module.guarantee.integtests.GuaranteeModuleIntegTestAbstract;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.LeaseForOxfTopModel001Gb;
import org.estatio.module.party.dom.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class GuaranteeIntegration_IntegTest extends GuaranteeModuleIntegTestAbstract {

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    GuaranteeRepository guaranteeRepository;

    @Inject
    TransactionService transactionService;

    @Inject
    SudoService sudoService;

    @Inject
    UserService userService;

    @Inject
    private DomainObjectContainer container;

    @Inject
    AgreementRoleRepository agreementRoleRepository;

    Lease lease;

    Guarantee guaranteeWithFinancialAccount;

    Guarantee guaranteeWithoutFinancialAccount;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());
            }
        }.withTracing());

    }

    @Before
    public void setUp() {
        lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
        guaranteeWithFinancialAccount = guaranteeRepository.findByReference(LeaseForOxfTopModel001Gb.REF + "-D");
        GuaranteeType guaranteeType = GuaranteeType.UNKNOWN;
        guaranteeWithoutFinancialAccount = guaranteeRepository.newGuarantee(
                lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, VT.ld(2012,1,1), null, "", VT.bd(1000), null);
        transactionService.flushTransaction();
    }

    public static class Remove extends GuaranteeIntegration_IntegTest {

        @Test
        public void happy_case() throws Exception {
            //Given
            final Guarantee guarantee = guaranteeRepository.findByReference(GuaranteeForOxfTopModel001Gb.REFERENCE);
            assertThat(guarantee.getRoles()).isNotNull();
            final Party primaryParty = guarantee.getPrimaryParty();
            final int size = agreementRoleRepository.findByParty(primaryParty).size();


            //When
            sudoService.sudo("estatio-admin", Lists.newArrayList("estatio-admin"),
                    () -> wrap(guarantee).remove("Some reason"));

            //Then
            assertThat(guaranteeRepository.findByReference(GuaranteeForOxfTopModel001Gb.REFERENCE)).isNull();

            //TODO: The agreement roles are implicitly removed, scary....
            assertThat(agreementRoleRepository.findByParty(primaryParty).size()).isEqualTo(size-1);
        }

    }

    public static class ChangeGuaranteeType extends GuaranteeIntegration_IntegTest {

        @Test
        public void happyCase1() throws Exception {
            // when
            guaranteeWithoutFinancialAccount.changeGuaranteeType(GuaranteeType.COMPANY_GUARANTEE);

            // then
            assertThat(guaranteeWithoutFinancialAccount.getGuaranteeType()).isEqualTo(GuaranteeType.COMPANY_GUARANTEE);
        }

        @Test
        public void happyCase2() throws Exception {
            // when
            guaranteeWithoutFinancialAccount.changeGuaranteeType(GuaranteeType.BANK_GUARANTEE);
            transactionService.flushTransaction();

            FinancialAccount financialAccount = guaranteeWithoutFinancialAccount.getFinancialAccount();
            Party secondaryParty = lease.getSecondaryParty();

            // then
            assertThat(guaranteeWithoutFinancialAccount.getGuaranteeType()).isEqualTo(GuaranteeType.BANK_GUARANTEE);
            assertThat(financialAccount).isNotNull();
            assertThat(financialAccount.getReference()).isEqualTo(guaranteeWithoutFinancialAccount.getReference());
            assertThat(financialAccount.getOwner()).isEqualTo(secondaryParty);
        }

        @Test(expected = DisabledException.class)
        public void cannot_change_without_administrator_role() throws Exception {
            sudoService.sudo("estatio-user", Lists.newArrayList("estatio-user"), new Runnable() {
                @Override public void run() {
                    wrap(guaranteeWithFinancialAccount).changeGuaranteeType(GuaranteeType.UNKNOWN);                }
            });
        }
    }

    public static class Terminate extends GuaranteeIntegration_IntegTest {

        @Test
        public void happyCaseWithFinancialAccount() throws Exception {
            // given
            assertThat(guaranteeWithFinancialAccount.getTerminationDate()).isNull();

            // when
            wrap(guaranteeWithFinancialAccount).terminate(new LocalDate(2016, 1, 1), "Test");

            // then
            assertThat(guaranteeWithFinancialAccount.getTerminationDate()).isEqualTo(new LocalDate(2016, 1, 1));
        }

        @Test
        public void happyCaseWithoutFinancialAccount() throws Exception {
            // given
            assertThat(guaranteeWithoutFinancialAccount.getTerminationDate()).isNull();
            assertThat(guaranteeWithoutFinancialAccount.getFinancialAccount()).isNull();

            // when
            wrap(guaranteeWithoutFinancialAccount).terminate(new LocalDate(2016, 1, 1), "Test");

            // then
            assertThat(guaranteeWithoutFinancialAccount.getTerminationDate()).isEqualTo(new LocalDate(2016, 1, 1));
        }
    }

}