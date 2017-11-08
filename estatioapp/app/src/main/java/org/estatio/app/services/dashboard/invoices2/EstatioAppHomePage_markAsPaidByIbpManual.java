package org.estatio.app.services.dashboard.invoices2;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.app.services.dashboard.EstatioAppHomePage;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_markAsPaidByIbpManual;

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public class EstatioAppHomePage_markAsPaidByIbpManual {

    private final EstatioAppHomePage homePage;

    public EstatioAppHomePage_markAsPaidByIbpManual(EstatioAppHomePage homePage) {
        this.homePage = homePage;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    public EstatioAppHomePage act(
            final List<IncomingInvoice> invoices,
            @Nullable
            final String comment) {

        for (IncomingInvoice invoice : invoices) {
            factoryService.mixin(IncomingInvoice_markAsPaidByIbpManual.class, invoice).act(comment);
        }

        return homePage;
    }
    public List<IncomingInvoice> choices0Act() {
        return homePage.getIncomingInvoicesPayableByManualProcess();
    }
    public List<IncomingInvoice> default0Act() {
        return choices0Act();
    }
    public boolean hideAct() {
        // hide (rather than disable) this action so that, once all these invoices have been dealt with, then the action will disappear.
        return choices0Act().isEmpty();
    }


    @Inject
    FactoryService factoryService;
}
