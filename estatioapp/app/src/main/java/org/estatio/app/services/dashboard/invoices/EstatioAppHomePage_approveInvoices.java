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
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;

/**
 * For testing only
 */
@Mixin(method = "act")
public class EstatioAppHomePage_approveInvoices {

    private final EstatioAppHomePage homePage;

    public EstatioAppHomePage_approveInvoices(EstatioAppHomePage homePage) {
        this.homePage = homePage;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    public EstatioAppHomePage act(
            final List<IncomingInvoice> invoices,
            @Nullable
            final String comment) {

        for (IncomingInvoice invoice : invoices) {
            factoryService.mixin(IncomingInvoice_approve.class, invoice).act(null, null, comment, false);
        }

        return homePage;
    }

    public List<IncomingInvoice> choices0Act() {
        return homePage.getIncomingInvoicesCompleted();
    }
    public List<IncomingInvoice> default0Act() {
        return choices0Act();
    }


    @Inject
    FactoryService factoryService;
}
