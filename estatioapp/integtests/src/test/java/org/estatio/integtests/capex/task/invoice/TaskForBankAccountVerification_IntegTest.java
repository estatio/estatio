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
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.roles.EstatioRole;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountForTopModelGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState.CANCELLED;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState.PENDING;
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
            assertState(bankAccount, PENDING);

            // and then also
            final List<BankAccountVerificationStateTransition> transitionsAfter =
                    bankAccountVerificationTransitionRepository.findByDomainObject(bankAccount);
            assertThat(transitionsAfter.size()).isEqualTo(2);
            assertTransition(transitionsAfter.get(0), PENDING, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitionsAfter.get(1), null, INSTANTIATE, PENDING);


            // and then also
            final Task task = transitionsAfter.get(0).getTask();
            assertThat(task).isNotNull();
            assertThat(task.getAssignedTo()).isEqualTo(EstatioRole.TREASURER);

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

            assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(PENDING);
        }

        @Test
        public void verify() throws Exception {

            // given
            assertState(bankAccount, PENDING);

            final List<BankAccountVerificationStateTransition> transitionsBefore = findTransitions(bankAccount);
            assertThat(transitionsBefore.size()).isEqualTo(2);
            assertTransition(transitionsBefore.get(0), PENDING, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitionsBefore.get(1), null, INSTANTIATE, PENDING);

            // when
            getFixtureClock().addTime(0,1);
            wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);

            // then
            assertState(bankAccount, VERIFIED);

            final List<BankAccountVerificationStateTransition> transitionsAfter = findTransitions(bankAccount);
            assertThat(transitionsAfter.size()).isEqualTo(2);
            assertTransition(transitionsAfter.get(0), PENDING, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitionsBefore.get(1), null, INSTANTIATE, PENDING);
        }


        @Test
        public void cancel_when_pending() throws Exception {

            // given
            assertState(bankAccount, PENDING);

            final List<BankAccountVerificationStateTransition> transitionsBefore = findTransitions(bankAccount);
            assertThat(transitionsBefore.size()).isEqualTo(2);
            assertTransition(transitionsBefore.get(0), PENDING, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitionsBefore.get(1), null, INSTANTIATE, PENDING);

            // when
            getFixtureClock().addTime(0,1);
            wrap(mixin(BankAccount_cancel.class, bankAccount)).act(null);

            // then
            assertState(bankAccount, CANCELLED);

            final List<BankAccountVerificationStateTransition> transitionsAfter = findTransitions(bankAccount);
            assertThat(transitionsAfter.size()).isEqualTo(2);
            assertTransition(transitionsAfter.get(0), PENDING, CANCEL, CANCELLED);
            assertTransition(transitionsAfter.get(1), null, INSTANTIATE, PENDING);
        }

        @Test
        public void cannot_cancel_when_verified() throws Exception {

            // given
            getFixtureClock().addTime(0,1);
            wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);

            assertState(bankAccount, VERIFIED);

            final List<BankAccountVerificationStateTransition> transitionsBefore = findTransitions(this.bankAccount);
            assertThat(transitionsBefore.size()).isEqualTo(2);
            assertTransition(transitionsBefore.get(0), PENDING, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitionsBefore.get(1), null, INSTANTIATE, PENDING);

            // expect
            expectedExceptions.expect(HiddenException.class);

            // when
            getFixtureClock().addTime(0,1);
            wrap(mixin(BankAccount_cancel.class, this.bankAccount)).act(null);
        }

        @Test
        public void change_when_still_pending_will_reset() throws Exception {

            // given
            assertState(this.bankAccount, PENDING);

            final List<BankAccountVerificationStateTransition> transitionsBefore = findTransitions(bankAccount);
            assertThat(transitionsBefore.size()).isEqualTo(2);
            assertTransition(transitionsBefore.get(0), PENDING, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitionsBefore.get(1), null, INSTANTIATE, PENDING);

            // when
            getFixtureClock().addTime(0,1);
            final String validIban = "NL39ABNA0572008761";
            wrap(bankAccount).change(validIban, bankAccount.getBic(), "changed-external-reference");

            // then
            assertState(this.bankAccount, PENDING);
            final List<BankAccountVerificationStateTransition> transitionsAfter = findTransitions(bankAccount);
            assertThat(transitionsAfter.size()).isEqualTo(3);
            assertTransition(transitionsAfter.get(0), PENDING, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitionsAfter.get(1), PENDING, RESET, PENDING);
            assertTransition(transitionsAfter.get(2), null, INSTANTIATE, PENDING);
        }

        @Test
        public void change_when_verified_will_reset() throws Exception {

            // given
            getFixtureClock().addTime(0,1);
            wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);

            assertState(this.bankAccount, VERIFIED);

            final List<BankAccountVerificationStateTransition> transitionsBefore = findTransitions(bankAccount);
            assertThat(transitionsBefore.size()).isEqualTo(2);
            assertTransition(transitionsBefore.get(0), PENDING, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitionsBefore.get(1), null, INSTANTIATE, PENDING);

            // when
            getFixtureClock().addTime(0,1);
            final String validIban = "NL39ABNA0572008761";
            wrap(bankAccount).change(validIban, bankAccount.getBic(), "changed-external-reference");

            // then
            assertState(this.bankAccount, PENDING);

            final List<BankAccountVerificationStateTransition> transitionsAfter = findTransitions(bankAccount);
            assertThat(transitionsAfter.size()).isEqualTo(4);
            assertTransition(transitionsAfter.get(0), PENDING, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitionsAfter.get(1), VERIFIED, RESET, PENDING);
            assertTransition(transitionsAfter.get(2), PENDING, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitionsAfter.get(3), null, INSTANTIATE, PENDING);
        }

        @Test
        public void change_when_cancelled_will_reset() throws Exception {

            // given
            getFixtureClock().addTime(0,1);
            wrap(mixin(BankAccount_cancel.class, bankAccount)).act(null);

            assertState(this.bankAccount, CANCELLED);

            final List<BankAccountVerificationStateTransition> transitionsBefore = findTransitions(bankAccount);
            assertThat(transitionsBefore.size()).isEqualTo(2);
            assertTransition(transitionsBefore.get(0), PENDING, CANCEL, CANCELLED);
            assertTransition(transitionsBefore.get(1), null, INSTANTIATE, PENDING);

            // when
            getFixtureClock().addTime(0,1);
            final String validIban = "NL39ABNA0572008761";
            wrap(bankAccount).change(validIban, bankAccount.getBic(), "changed-external-reference");

            // then
            assertState(this.bankAccount, PENDING);

            final List<BankAccountVerificationStateTransition> transitionsAfter = findTransitions(bankAccount);
            assertThat(transitionsAfter.size()).isEqualTo(4);
            assertTransition(transitionsAfter.get(0), PENDING, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitionsAfter.get(1), CANCELLED, RESET, PENDING);
            assertTransition(transitionsAfter.get(2), PENDING, CANCEL, CANCELLED);
            assertTransition(transitionsAfter.get(3), null, INSTANTIATE, PENDING);
        }
    }
}