package org.estatio.capex.dom.documents.categorisation.invoice;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Party;

public interface SellerProvider {
    Party getSeller();
    void setBankAccount(BankAccount bankAccount);
}
