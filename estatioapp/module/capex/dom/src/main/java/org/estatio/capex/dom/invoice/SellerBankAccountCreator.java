package org.estatio.capex.dom.invoice;

import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Party;

public interface SellerBankAccountCreator {
    Party getSeller();
    IncomingInvoiceApprovalState getApprovalState();
    void setBankAccount(BankAccount bankAccount);
}
