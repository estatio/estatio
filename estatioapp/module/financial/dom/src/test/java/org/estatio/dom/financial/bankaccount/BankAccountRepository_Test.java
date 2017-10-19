package org.estatio.dom.financial.bankaccount;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.financial.FinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;

public class BankAccountRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    FinancialAccountRepository mockFARepo;

    @Test
    public void findBankAccountsByOwner_filters_deprecated() throws Exception {

        // given
        BankAccountRepository repo = new BankAccountRepository();
        repo.financialAccountRepository = mockFARepo;
        Party party = new Organisation();
        BankAccount account1 = new BankAccount();
        BankAccount account2 = new BankAccount();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockFARepo).findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party);
            will(returnValue(Arrays.asList(account1, account2)));
        }});

        // when
        List<BankAccount> result = repo.findBankAccountsByOwner(party);
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).contains(account1);
        Assertions.assertThat(result).contains(account2);

        // and given
        account2.setDeprecated(true);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockFARepo).findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party);
            will(returnValue(Arrays.asList(account1, account2)));
        }});

        // when
        result = repo.findBankAccountsByOwner(party);
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result).contains(account1);
        Assertions.assertThat(result).doesNotContain(account2);
    }

    @Test
    public void autoComplete_filters_deprecated() throws Exception {

        // given
        BankAccount account1 = new BankAccount();
        BankAccount account2 = new BankAccount();
        BankAccountRepository repo = new BankAccountRepository(){
            @Override
            public List<BankAccount> findByReferenceMatches(final String regex) {
                return Arrays.asList(account1, account2);
            }
        };


        // when
        List<BankAccount> result = repo.autoComplete("");
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).contains(account1);
        Assertions.assertThat(result).contains(account2);

        // and given
        account2.setDeprecated(true);

        // when
        result = repo.autoComplete("");
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result).contains(account1);
        Assertions.assertThat(result).doesNotContain(account2);
    }

}