package org.estatio.capex.dom.bankaccount.verification;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method="coll")
public class BankAccount_incomingInvoices {

    private final BankAccount bankAccount;

    public BankAccount_incomingInvoices(final BankAccount bankAccount) {

        this.bankAccount = bankAccount;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<IncomingInvoice> coll() {
        return incomingInvoiceRepository.findByBankAccount(bankAccount);
    }


    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;
}
