package org.estatio.module.application.app.dashboard.invoices;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.module.application.app.dashboard.EstatioAppHomePage;
import org.estatio.module.capex.dom.bankaccount.verification.triggers.BankAccount_verify;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.tasks.IncomingInvoice_checkApprovalState;
import org.estatio.module.financial.dom.BankAccount;

/**
 * For testing only
 *
 * this could be inlined, but perhaps should not given that it is for testing/prototyping only?
 */
@Mixin(method = "act")
public class EstatioAppHomePage_verifyBankAccountsFra {

    private final EstatioAppHomePage homePage;

    public EstatioAppHomePage_verifyBankAccountsFra(EstatioAppHomePage homePage) {
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
        return homePage.getIncomingInvoicesFraPendingBankAccountCheck();
    }
    public List<IncomingInvoice> default0Act() {
        return choices0Act();
    }


    @Inject
    FactoryService factoryService;
}
