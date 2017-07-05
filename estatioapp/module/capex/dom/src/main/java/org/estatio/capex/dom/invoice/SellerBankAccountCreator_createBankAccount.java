package org.estatio.capex.dom.invoice;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.financial.utils.IBANValidator;

@Mixin(method = "act")
public class SellerBankAccountCreator_createBankAccount {
    private final SellerBankAccountCreator sellerBankAccountCreator;

    public SellerBankAccountCreator_createBankAccount(final SellerBankAccountCreator sellerBankAccountCreator) {
        this.sellerBankAccountCreator = sellerBankAccountCreator;
    }

    public SellerBankAccountCreator act(final String ibanNumber) {
        bankAccountRepository
                .newBankAccount(sellerBankAccountCreator.getSeller(), ibanNumber, null);
        final BankAccount bankAccount = bankAccountRepository.findBankAccountByReference(sellerBankAccountCreator.getSeller(), ibanNumber); // not exactly sure why, but this lookup is required (instead of setting bankaccount right away) in order that jaxb viewmodel is recreated correctly (otherwise we get an empty bank account).
        sellerBankAccountCreator.setBankAccount(bankAccount);
        return sellerBankAccountCreator;
    }

    public String disableAct() {
        if (sellerBankAccountCreator.getSeller() == null) {
            return "There is no seller specified";
        }
        return null;
    }

    public String validate0Act(final String ibanNumber) {
        if (!IBANValidator.valid(ibanNumber)) {
            return String.format("%s is not a valid iban number", ibanNumber);
        }
        if (bankAccountRepository.findBankAccountByReference(sellerBankAccountCreator.getSeller(), ibanNumber) != null) {
            return String.format("%s has already bank account with iban %s", sellerBankAccountCreator.getSeller().getName(),
                    ibanNumber);
        }
        return null;
    }

    @Inject
    protected BankAccountRepository bankAccountRepository;
}
