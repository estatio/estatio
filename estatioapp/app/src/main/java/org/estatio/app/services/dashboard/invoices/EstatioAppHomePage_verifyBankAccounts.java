package org.estatio.app.services.dashboard.invoices;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.app.services.dashboard.EstatioAppHomePage;
import org.estatio.capex.dom.bankaccount.verification.triggers.BankAccount_verify;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.tasks.IncomingInvoice_checkApprovalState;
import org.estatio.dom.financial.bankaccount.BankAccount;

/**
 * For testing only
 *
 * this could be inlined, but perhaps should not given that it is for testing/prototyping only?
 */
@Mixin(method = "act")
public class EstatioAppHomePage_verifyBankAccounts {

    private final EstatioAppHomePage homePage;

    public EstatioAppHomePage_verifyBankAccounts(EstatioAppHomePage homePage) {
        this.homePage = homePage;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    public EstatioAppHomePage act(
            final List<IncomingInvoice> invoices,
            @Nullable
            final String comment) {

        for (IncomingInvoice invoice : invoices) {
            final BankAccount bankAccount = invoice.getBankAccount();
            factoryService.mixin(BankAccount_verify.class, bankAccount).act(comment);
            factoryService.mixin(IncomingInvoice_checkApprovalState.class, invoice).act();
        }

        return homePage;
    }

    public List<IncomingInvoice> choices0Act() {
        return homePage.getIncomingInvoicesPendingBankAccountCheck();
    }
    public List<IncomingInvoice> default0Act() {
        return choices0Act();
    }


    @Inject
    FactoryService factoryService;
}
