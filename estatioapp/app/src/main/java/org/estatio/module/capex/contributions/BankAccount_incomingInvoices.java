package org.estatio.module.capex.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.financial.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know about incoming invoices or documents.
 */
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
