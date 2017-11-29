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
package org.estatio.module.application.integtests.capex.task.invoice;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.HiddenException;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.application.integtests.ApplicationModuleIntegTestAbstract;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccount_enum;
import org.estatio.module.capex.contributions.BankAccount_attachPdfAsIbanProof;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccount_verificationState;
import org.estatio.module.capex.dom.bankaccount.verification.triggers.BankAccount_rejectProof;
import org.estatio.module.capex.dom.bankaccount.verification.triggers.BankAccount_verify;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.integtests.document.IncomingDocumentPresentationSubscriber_IntegTest;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfTopModel001Gb;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState.AWAITING_PROOF;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState.NOT_VERIFIED;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState.VERIFIED;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.INSTANTIATE;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.PROOF_UPDATED;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.REJECT_PROOF;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.RESET;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT;

public class TaskForBankAccountVerification_IntegTest extends ApplicationModuleIntegTestAbstract {



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
        Assertions.assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(
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

                    executionContext.executeChild(this, Organisation_enum.TopModelGb.toFixtureScript());
                }
            });

            // given
            seller = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        }

        @Test
        public void happy_case() throws Exception {

            // when
            BankAccount bankAccount = bankAccountRepository.newBankAccount(seller,
                    BankAccount_enum.TopModelGb.getIban(), "12345");

            // then
            assertState(bankAccount, NOT_VERIFIED);

            // and then also
            List<BankAccountVerificationStateTransition> transitions =
                    bankAccountVerificationTransitionRepository.findByDomainObject(bankAccount);
            assertThat(transitions.size()).isEqualTo(1);
            assertTransition(transitions.get(0), null, INSTANTIATE, NOT_VERIFIED);

            // and when
            final BankAccountVerificationState currentState = stateTransitionService
                    .currentStateOf(bankAccount, VERIFY_BANK_ACCOUNT);
            final Person personToAssignNextToIfAny = null;
            stateTransitionService.createPendingTransition(bankAccount, NOT_VERIFIED, VERIFY_BANK_ACCOUNT,
                    personToAssignNextToIfAny, null);
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
            assertThat(task.getAssignedTo().getKey()).isEqualTo(PartyRoleTypeEnum.TREASURER.getKey());

        }

    }

    public static class WhenChangeBankAccount extends TaskForBankAccountVerification_IntegTest {

        Party seller;
        BankAccount bankAccount;

        @Before
        public void setupData() throws IOException {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final FixtureScript.ExecutionContext executionContext) {

                    executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, BankAccount_enum.TopModelGb.toFixtureScript());
                }
            });

            // given
            seller = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
            bankAccount = bankAccountRepository.findBankAccountByReference(seller,
                    BankAccount_enum.TopModelGb.getIban());
            // bank accounts now need BICs so can verify
            bankAccount.setBic("123456789");

            final String fileName = "1020100123.pdf";
            final byte[] pdfBytes = Resources.toByteArray(
                    Resources.getResource(IncomingDocumentPresentationSubscriber_IntegTest.class, fileName));
            final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);

            wrap(mixin(BankAccount_attachPdfAsIbanProof.class, bankAccount)).act(blob);

            Assertions.assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(NOT_VERIFIED);
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
            final Person personToAssignNextToIfAny = null;
            stateTransitionService.createPendingTransition(bankAccount, NOT_VERIFIED, VERIFY_BANK_ACCOUNT,
                    personToAssignNextToIfAny, null);
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
        public void can_reject_proof_when_not_verified() throws Exception {

            // given
            assertState(bankAccount, NOT_VERIFIED);

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(1);
            assertTransition(transitions.get(0), null, INSTANTIATE, NOT_VERIFIED);

            // when
            wrap(mixin(BankAccount_rejectProof.class, bankAccount)).act("SOME ROLE", null, "bad proof, bad!");
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(3);
            assertTransition(transitions.get(0), AWAITING_PROOF, PROOF_UPDATED, null);
            assertTransition(transitions.get(1), NOT_VERIFIED, REJECT_PROOF, AWAITING_PROOF);
            assertTransition(transitions.get(2), null, INSTANTIATE, NOT_VERIFIED);

            assertState(bankAccount, AWAITING_PROOF);
        }

        @Test
        public void can_reject_proof_when_not_verified_and_a_pending_transition_has_been_setup_somehow() throws Exception {

            // given
            assertState(bankAccount, NOT_VERIFIED);
            final Person personToAssignNextToIfAny = null;
            stateTransitionService.createPendingTransition(bankAccount, NOT_VERIFIED, VERIFY_BANK_ACCOUNT,
                    personToAssignNextToIfAny, null);
            transactionService.nextTransaction();

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            // when
            wrap(mixin(BankAccount_rejectProof.class, bankAccount)).act("SOME ROLE", null, "bad proof, bad!");
            transactionService.nextTransaction();

            // then
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(3);
            assertTransition(transitions.get(0), AWAITING_PROOF, PROOF_UPDATED, null);
            assertTransition(transitions.get(1), NOT_VERIFIED, REJECT_PROOF, AWAITING_PROOF);
            assertTransition(transitions.get(2), null, INSTANTIATE, NOT_VERIFIED);

            assertState(bankAccount, AWAITING_PROOF);
        }

        @Test
        public void cannot_reject_proof_when_verified() throws Exception {

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
            wrap(mixin(BankAccount_rejectProof.class, this.bankAccount)).act(null, null, "bad!!!");
        }

        @Test
        public void change_when_still_pending_will_reset() throws Exception {

            // given
            assertState(this.bankAccount, NOT_VERIFIED);
            final Person personToAssignNextToIfAny = null;
            stateTransitionService.createPendingTransition(bankAccount, NOT_VERIFIED, VERIFY_BANK_ACCOUNT,
                    personToAssignNextToIfAny, null);
            transactionService.nextTransaction();

            List<BankAccountVerificationStateTransition> transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

            // when
            final String validIban = "NL39ABNA0572008761";
            final String changedExternalReference = "changed-external-reference";
            wrap(bankAccount).change(validIban, bankAccount.getBic(), changedExternalReference);
            transactionService.nextTransaction();

            // then
            assertThat(bankAccount.getExternalReference()).isEqualTo(changedExternalReference);

            // then no new transition, though (previously we used to create a transition of NOT_VERIFIED -> NOT_VERIFIED)
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(2);
            assertTransition(transitions.get(0), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, null);
            assertTransition(transitions.get(1), null, INSTANTIATE, NOT_VERIFIED);

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

            // then back to not verified.
            // (Previously we also created a pending transition back to verified, but no longer bother;
            // there is no task associated, and the user just did a reset anyway...)
            transitions = findTransitions(bankAccount);
            assertThat(transitions.size()).isEqualTo(3);
            assertTransition(transitions.get(0), VERIFIED, RESET, NOT_VERIFIED);
            assertTransition(transitions.get(1), NOT_VERIFIED, VERIFY_BANK_ACCOUNT, VERIFIED);
            assertTransition(transitions.get(2), null, INSTANTIATE, NOT_VERIFIED);

            assertState(this.bankAccount, NOT_VERIFIED);
        }


    }
}