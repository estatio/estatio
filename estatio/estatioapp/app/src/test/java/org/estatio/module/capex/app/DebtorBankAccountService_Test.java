package org.estatio.module.capex.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccountRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class DebtorBankAccountService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock BankAccountRepository mockBankAccountRepository;

    @Test
    public void unique_Debtor_Account_To_Pay_works_when_no_bankaccount_and_no_property() throws Exception {

        // given
        DebtorBankAccountService service = new DebtorBankAccountService();
        service.bankAccountRepository = mockBankAccountRepository;
        IncomingInvoice invoice = new IncomingInvoice();
        Party debtor = new Organisation();
        invoice.setBuyer(debtor);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBankAccountRepository).findBankAccountsByOwner(debtor);
        }});

        // when, then
        assertThat(service.uniqueDebtorAccountToPay(invoice)).isNull();

    }

    @Mock
    FixedAssetFinancialAccountRepository mockFixedAssetFinancialAccountRepository;

    @Test
    public void unique_Debtor_Account_To_Pay_works_when_no_bankaccount_and_property() throws Exception {

        // given
        DebtorBankAccountService service = new DebtorBankAccountService();
        service.bankAccountRepository = mockBankAccountRepository;
        service.fixedAssetFinancialAccountRepository = mockFixedAssetFinancialAccountRepository;
        IncomingInvoice invoice = new IncomingInvoice();
        Party debtor = new Organisation();
        invoice.setBuyer(debtor);
        Property property = new Property();
        invoice.setProperty(property);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBankAccountRepository).findBankAccountsByOwner(debtor);
            oneOf(mockFixedAssetFinancialAccountRepository).findByFixedAsset(property);
        }});

        // when, then
        assertThat(service.uniqueDebtorAccountToPay(invoice)).isNull();

    }

    @Test
    public void unique_Debtor_Account_To_Pay_works_when_one_bankaccount_and_no_property() throws Exception {

        // given
        DebtorBankAccountService service = new DebtorBankAccountService();
        service.bankAccountRepository = mockBankAccountRepository;

        BankAccount bankAccount = new BankAccount();

        IncomingInvoice invoice = new IncomingInvoice();
        Party debtor = new Organisation();
        invoice.setBuyer(debtor);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBankAccountRepository).findBankAccountsByOwner(debtor);
            will(returnValue(Arrays.asList(bankAccount)));
        }});

        // when, then
        assertThat(service.uniqueDebtorAccountToPay(invoice)).isEqualTo(bankAccount);

    }

    @Test
    public void unique_Debtor_Account_To_Pay_works_when_bankaccounts_and_one_for_property() throws Exception {

        // given
        DebtorBankAccountService service = new DebtorBankAccountService();
        service.bankAccountRepository = mockBankAccountRepository;
        service.fixedAssetFinancialAccountRepository = mockFixedAssetFinancialAccountRepository;

        BankAccount bankAccount = new BankAccount();
        BankAccount bankAccountForProperty = new BankAccount();
        FixedAssetFinancialAccount fixedAssetFinancialAccount = new FixedAssetFinancialAccount();
        fixedAssetFinancialAccount.setFinancialAccount(bankAccountForProperty);

        List<BankAccount> bankAccounts = new ArrayList<>();
        bankAccounts.add(bankAccount);
        bankAccounts.add(bankAccountForProperty);

        IncomingInvoice invoice = new IncomingInvoice();
        Party debtor = new Organisation();
        invoice.setBuyer(debtor);
        Property property = new Property();
        invoice.setProperty(property);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBankAccountRepository).findBankAccountsByOwner(debtor);
            will(returnValue(bankAccounts));
            oneOf(mockFixedAssetFinancialAccountRepository).findByFixedAsset(property);
            will(returnValue(Arrays.asList(fixedAssetFinancialAccount)));
        }});

        // when, then
        assertThat(service.uniqueDebtorAccountToPay(invoice)).isEqualTo(bankAccountForProperty);

    }

    @Test
    public void unique_Debtor_Account_To_Pay_works_when_multiple_bankaccounts_one_preferred() throws Exception {

        // given
        DebtorBankAccountService service = new DebtorBankAccountService();
        service.bankAccountRepository = mockBankAccountRepository;

        BankAccount bankAccount = new BankAccount();
        BankAccount preferredBankAccount = new BankAccount();
        preferredBankAccount.setPreferred(true);
        List<BankAccount> bankAccounts = new ArrayList<>();
        bankAccounts.add(bankAccount);
        bankAccounts.add(preferredBankAccount);

        IncomingInvoice invoice = new IncomingInvoice();
        Party debtor = new Organisation();
        invoice.setBuyer(debtor);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBankAccountRepository).findBankAccountsByOwner(debtor);
            will(returnValue(bankAccounts));
        }});

        // when, then
        assertThat(service.uniqueDebtorAccountToPay(invoice)).isEqualTo(preferredBankAccount);

    }

    @Test
    public void unique_Debtor_Account_To_Pay_works_when_multiple_bankaccounts_none_preferred() throws Exception {

        // given
        DebtorBankAccountService service = new DebtorBankAccountService();
        service.bankAccountRepository = mockBankAccountRepository;

        BankAccount bankAccount1 = new BankAccount();
        BankAccount bankAccount2 = new BankAccount();
        List<BankAccount> bankAccounts = new ArrayList<>();
        bankAccounts.add(bankAccount1);
        bankAccounts.add(bankAccount2);

        IncomingInvoice invoice = new IncomingInvoice();
        Party debtor = new Organisation();
        invoice.setBuyer(debtor);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBankAccountRepository).findBankAccountsByOwner(debtor);
            will(returnValue(bankAccounts));
        }});

        // when, then
        assertThat(service.uniqueDebtorAccountToPay(invoice)).isNull();

    }

    @Test
    public void unique_Debtor_Account_To_Pay_works_when_multiple_bankaccounts_multiple_preferred() throws Exception {

        // given
        DebtorBankAccountService service = new DebtorBankAccountService();
        service.bankAccountRepository = mockBankAccountRepository;

        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setPreferred(true);
        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setPreferred(true);
        List<BankAccount> bankAccounts = new ArrayList<>();
        bankAccounts.add(bankAccount1);
        bankAccounts.add(bankAccount2);

        IncomingInvoice invoice = new IncomingInvoice();
        Party debtor = new Organisation();
        invoice.setBuyer(debtor);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBankAccountRepository).findBankAccountsByOwner(debtor);
            will(returnValue(bankAccounts));
        }});

        // when, then
        assertThat(service.uniqueDebtorAccountToPay(invoice)).isNull();

    }

}