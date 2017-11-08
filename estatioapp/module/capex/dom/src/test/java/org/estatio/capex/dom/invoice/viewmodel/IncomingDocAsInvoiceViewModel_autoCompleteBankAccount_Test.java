package org.estatio.capex.dom.invoice.viewmodel;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.bankaccount.dom.BankAccountRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocAsInvoiceViewModel_autoCompleteBankAccount_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private OrderRepository mockOrderRepository;

    @Mock
    private BankAccountRepository mockBankAccountRepository;

    @Test
    public void autoCompleteBankAccount_works() throws Exception {

        List<BankAccount> result;

        // given
        IncomingDocAsInvoiceViewModel vm = new IncomingDocAsInvoiceViewModel() {
            IncomingDocAsInvoiceViewModel setBankAccountRepository(BankAccountRepository bankAccountRepository) {
                this.bankAccountRepository = bankAccountRepository;
                return this;
            }
        }.setBankAccountRepository(mockBankAccountRepository);

        BankAccount acc1 = new BankAccount();
        BankAccount acc2 = new BankAccount();

        Party owner = new Organisation();
        acc2.setOwner(owner);

        // expect
        context.checking(new Expectations() {
            {
                allowing(mockBankAccountRepository).autoComplete(with(any(String.class)));
                will(returnValue(Arrays.asList(
                        acc1, acc2
                )));
                oneOf(mockBankAccountRepository).findBankAccountsByOwner(owner);
                will(returnValue(Arrays.asList(
                        acc2
                )));
            }

        });

        // when
        result = vm.autoCompleteBankAccount("some searchstring");

        // then
        assertThat(result.size()).isEqualTo(2);

        // and when seller is set
        vm.setSeller(owner);
        result = vm.autoCompleteBankAccount("3");

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(acc2);

    }
}