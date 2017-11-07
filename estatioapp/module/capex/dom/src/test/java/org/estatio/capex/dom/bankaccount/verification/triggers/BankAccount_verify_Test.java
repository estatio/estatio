package org.estatio.capex.dom.bankaccount.verification.triggers;

import java.util.List;

import org.assertj.core.util.Lists;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.bankaccount.documents.BankAccount_attachPdfAsIbanProof;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.bankaccount.dom.paperclips.PaperclipForBankAccount;

import static org.assertj.core.api.Assertions.assertThat;

public class BankAccount_verify_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    BankAccount bankAccount;
    BankAccount_verify mixin;

    @Mock
    PaperclipRepository mockPaperclipRepository;

    @Before
    public void setUp() throws Exception {

        bankAccount = new BankAccount();
        mixin = new BankAccount_verify(bankAccount);

        mixin.paperclipRepository = mockPaperclipRepository;

    }

    public static class DisablementTest extends BankAccount_verify_Test {

        @Test
        public void when_no_BIC() throws Exception {

            // when
            final String reason = mixin.disableAct();

            // then
            assertThat(reason).isEqualTo("BIC is required");
        }

        @Test
        public void when_no_attachments() throws Exception {

            // given
            bankAccount.setBic("12345");

            // expecting
            allowingPaperclipRepositoryToReturn(Lists.newArrayList());

            // when
            final String reason = mixin.disableAct();

            // then
            assertThat(reason).isEqualTo("IBAN proof must first be attached");
        }


        @Test
        public void when_BIC_and_attachments() throws Exception {

            // given
            bankAccount.setBic("12345");

            // expecting
            allowingPaperclipRepositoryToReturn(Lists.newArrayList(new PaperclipForBankAccount()));

            // when
            final String reason = mixin.disableAct();

            // then
            assertThat(reason).isNull();
        }

        void allowingPaperclipRepositoryToReturn(final List<Paperclip> result) {
            context.checking(new Expectations() {{
                allowing(mockPaperclipRepository).findByAttachedToAndRoleName(bankAccount,
                        BankAccount_attachPdfAsIbanProof.ROLE_NAME_FOR_IBAN_PROOF);
                will(returnValue(result));
            }});
        }

    }

}