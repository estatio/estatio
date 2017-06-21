package org.estatio.dom.financial.contributed;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;

import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.financial.utils.IBANValidator;
import org.estatio.dom.party.Party;

@Mixin
public class Party_newBankAccount {

    private final Party party;

    public Party_newBankAccount(Party party) {
        this.party = party;
    }

    @Action()
    @MemberOrder(name = "financialAccounts", sequence = "1")
    public Party $$(
            final String iban,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String bic) {
        bankAccountRepository.newBankAccount(party, iban, bic);
        return party;
    }

    public String validate0$$(final String iban) {
        if (!IBANValidator.valid(iban)) {
            return "Not a valid IBAN number";
        }
        return null;
    }


    @Inject
    private BankAccountRepository bankAccountRepository;

}
