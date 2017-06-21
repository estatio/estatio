package org.estatio.capex.dom.documents.categorisation.invoice;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.financial.utils.IBANValidator;

@Mixin(method = "act")
public class SellerProvider_createBankAccount {
    private final SellerProvider sellerProvider;

    public SellerProvider_createBankAccount(final SellerProvider sellerProvider) {
        this.sellerProvider = sellerProvider;
    }

    public SellerProvider act(final String ibanNumber) {
        BankAccount bankAccount = bankAccountRepository
                .newBankAccount(sellerProvider.getSeller(), ibanNumber, null);
        sellerProvider.setBankAccount(bankAccount);
        return sellerProvider;
    }

    public String disableAct() {
        if (sellerProvider.getSeller() == null) {
            return "There is no seller specified";
        }
        return null;
    }

    public String validate0Act(final String ibanNumber) {
        if (!IBANValidator.valid(ibanNumber)) {
            return String.format("%s is not a valid iban number", ibanNumber);
        }
        if (bankAccountRepository.findBankAccountByReference(sellerProvider.getSeller(), ibanNumber) != null) {
            return String.format("%s has already bank account with iban %s", sellerProvider.getSeller().getName(),
                    ibanNumber);
        }
        return null;
    }

    @Inject
    protected BankAccountRepository bankAccountRepository;
}
