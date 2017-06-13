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
package org.estatio.integtests.capex.task.invoice;

import java.util.List;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.HiddenException;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.bankaccount.verification.BankAccount_verificationState;
import org.estatio.capex.dom.bankaccount.verification.triggers.BankAccount_cancel;
import org.estatio.capex.dom.bankaccount.verification.triggers.BankAccount_verify;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.relationship.PartyRelationshipTypeEnum;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountForTopModelGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.capex.TickingFixtureClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState.CANCELLED;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState.NOT_VERIFIED;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState.VERIFIED;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.CANCEL;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.INSTANTIATE;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.RESET;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT;

public class TaskForBankAccountVerification_IntegTest extends EstatioIntegrationTest {



    @Inject
    PartyRepository partyRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    BankAccountVerificationStateTransition.Repository bankAccountVerificationTransitionRepository;

    @Inject
    StateTransitionService stateTransitionService;


    static void assertTransition(
            final BankAccountVerificationStateTransition transition,
            final BankAccountVerificationState from,
            final BankAccountVerificationStateTransitionType type,
            final BankAccountVerificationState to) {

        assertThat(transition.getTransitionType()).isEqualTo(type);
        if(from != null) {
            assertThat(transition.getFromState()).isEqualTo(from);
        } else {
            assertThat(transition.getFromState()).isNull();
        }
        if(to != null) {
            assertThat(transition.getToState()).isEqualTo(to);
        } else {
            assertThat(transition.getToState()).isNull();
        }
    }

    void assertState(final BankAccount bankAccount, final BankAccountVerificationState expected) {
        assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(
                expected);
    }


    protected List<BankAccountVerificationStateTransition> findTransitions(final BankAccount bankAccount) {
        return bankAccountVerificationTransitionRepository.findByDomainObject(bankAccount);
    }

    public static class WhenCreateBankAccount extends TaskForBankAccountVerification_IntegTest {

        Party seller;

        @Before
        public void setupData() {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final FixtureScript.ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new OrganisationForTopModelGb());
                }
            });

            // given
            seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
        }

        @Test
        public void happy_case() throws Exception {

            // when
            BankAccount bankAccount = bankAccountRepository.newBankAccount(seller, BankAccountForTopModelGb.REF, "12345");

            // then
            assertState(bankAccount, NOT_VERIFIED);

            // and then also
            List<BankAccountVerificationStateTransition> transitions =
                    bankAccountVerificationTransitionRepository.findByDomainObject(bankAccount);
            assertThat(transitions.size()).isEqualTo(1);
            assertTransition(transitions.get(0), null, INSTANTIATE, NOT_VERIFIED);

            // and when
            stateTransitionService.createPendingTransition(bankAccount, VERIFY_BANK_ACCOUNT);
            transactionService.nextTransaction();

            // then
            transitions =
                    bankAccountVerificationTransitionRepository.findByDomainObject(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            // and then also
            final Task task = transitions.get(0).getTask();
            assertThat(task).isNotNull();
            assertThat(task.getAssignedTo().getKey()).isEqualTo(PartyRelationshipTypeEnum.TREASURER.getKey());

        }

    }

    public static class WhenChangeBankAccount extends TaskForBankAccountVerification_IntegTest {

        Party seller;
        BankAccount bankAccount;

        @Before
        public void setupData() {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final FixtureScript.ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new BankAccountForTopModelGb());
                }
            });

            // given
            seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
            bankAccount = bankAccountRepository.findBankAccountByReference(seller, BankAccountForTopModelGb.REF);

            assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(NOT_VERIFIED);
        }

        @Before
        public void setupClock() {
            TickingFixtureClock.replaceExisting();
        }

        @After
        public void teardownClock() {
            TickingFixtureClock.reinstateExisting();
        }

        @Test
        public void verify_without_pending() throws Exception {

            // given
            assertState(bankAccount, NOT_VERIFIED);

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(1);
            assertTransition(transitions.get(0), null, INSTANTIATE, NOT_VERIFIED);

            // when
            wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            assertState(bankAccount, VERIFIED);
        }

        @Test
        public void verify_with_pending() throws Exception {

            // given
            assertState(bankAccount, NOT_VERIFIED);
            stateTransitionService.createPendingTransition(bankAccount, VERIFY_BANK_ACCOUNT);
            transactionService.nextTransaction();

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);


            // when
            wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            assertState(bankAccount, VERIFIED);
        }


        @Test
        public void cancel_without_pending() throws Exception {

            // given
            assertState(bankAccount, NOT_VERIFIED);

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(1);
            assertTransition(transitions.get(0), null, INSTANTIATE, NOT_VERIFIED);

            // when
            wrap(mixin(BankAccount_cancel.class, bankAccount)).act(null);
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, CANCEL, CANCELLED);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            assertState(bankAccount, CANCELLED);
        }

        @Test
        public void cancel_when_pending() throws Exception {

            // given
            assertState(bankAccount, NOT_VERIFIED);
            stateTransitionService.createPendingTransition(bankAccount, VERIFY_BANK_ACCOUNT);
            transactionService.nextTransaction();

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            // when
            wrap(mixin(BankAccount_cancel.class, bankAccount)).act(null);
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, CANCEL, CANCELLED);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            assertState(bankAccount, CANCELLED);
        }

        @Test
        public void cannot_cancel_when_verified() throws Exception {

            // given
            wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);
            transactionService.nextTransaction();

            assertState(bankAccount, VERIFIED);

            List<BankAccountVerificationStateTransition> transitions = findTransitions(this.bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            // expect
            expectedExceptions.expect(HiddenException.class);

            // when
            wrap(mixin(BankAccount_cancel.class, this.bankAccount)).act(null);
        }

        @Test
        public void change_when_still_pending_will_reset() throws Exception {

            // given
            assertState(this.bankAccount, NOT_VERIFIED);
            stateTransitionService.createPendingTransition(bankAccount, VERIFY_BANK_ACCOUNT);
            transactionService.nextTransaction();

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            // when
            final String validIban = "NL39ABNA0572008761";
            wrap(bankAccount).change(validIban, bankAccount.getBic(), "changed-external-reference");
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(3);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), NOT_VERIFIED, RESET, NOT_VERIFIED);
            assertTransition(transitions.get(2), null, INSTANTIATE, NOT_VERIFIED);

            assertState(this.bankAccount, NOT_VERIFIED);
        }

        @Test
        public void change_when_verified_will_reset() throws Exception {

            // given
            wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);
            transactionService.nextTransaction();

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            assertState(this.bankAccount, VERIFIED);

            // when
            final String validIban = "NL39ABNA0572008761";
            wrap(bankAccount).change(validIban, bankAccount.getBic(), "changed-external-reference");
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(4);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), VERIFIED, RESET, NOT_VERIFIED);
            assertTransition(transitions.get(2), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitions.get(3), null, INSTANTIATE, NOT_VERIFIED);

            assertState(this.bankAccount, NOT_VERIFIED);
        }

        @Test
        public void change_when_cancelled_will_reset() throws Exception {

            // given
            wrap(mixin(BankAccount_cancel.class, bankAccount)).act(null);
            transactionService.nextTransaction();

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, CANCEL, CANCELLED);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            assertState(this.bankAccount, CANCELLED);

            // when
            final String validIban = "NL39ABNA0572008761";
            wrap(bankAccount).change(validIban, bankAccount.getBic(), "changed-external-reference");
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(4);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), CANCELLED, RESET, NOT_VERIFIED);
            assertTransition(transitions.get(2), NOT_VERIFIED, CANCEL, CANCELLED);
            assertTransition(transitions.get(3), null, INSTANTIATE, NOT_VERIFIED);

            assertState(this.bankAccount, NOT_VERIFIED);
        }
    }
}