package org.estatio.capex.dom.invoice;

import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.party.dom.Party;

public interface SellerBankAccountCreator {
    Party getSeller();
    IncomingInvoiceApprovalState getApprovalState();
    void setBankAccount(BankAccount bankAccount);
}
