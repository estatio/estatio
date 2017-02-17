package org.estatio.app.menus.invoice;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.invoice.InvoiceRunType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;

@Mixin(method = "exec")
public class Lease_calculateLegacy {

    private final Lease lease;

    public Lease_calculateLegacy(final Lease lease) {
        this.lease = lease;
    }

    @Inject
    InvoiceServiceMenu invoiceServiceMenu;


    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    @ActionLayout(named = "Calculate")
    public Object exec(
            final InvoiceRunType runType,
            final InvoiceCalculationSelection calculationSelection,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        return invoiceServiceMenu.calculateLegacy(lease, runType, calculationSelection, invoiceDueDate, startDueDate, nextDueDate);
    }

    public InvoiceRunType default0Exec() {
        return invoiceServiceMenu.default1CalculateLegacy();
    }

    public InvoiceCalculationSelection default1Exec() {
        return invoiceServiceMenu.default2CalculateLegacy();
    }

    public LocalDate default2Exec() {
        return invoiceServiceMenu.default3CalculateLegacy();
    }

    public LocalDate default3Exec() {
        return invoiceServiceMenu.default4CalculateLegacy();
    }

    public LocalDate default4Exec() {
        return invoiceServiceMenu.default5CalculateLegacy();
    }

    public String validateExec(
            final InvoiceRunType runType,
            final InvoiceCalculationSelection selection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return invoiceServiceMenu.validateCalculateLegacy(lease, runType, selection, dueDate, startDate, endDate);
    }

    public String disableExec(){

        return invoiceServiceMenu.doDisableCalculate(lease);
    }

    public boolean hideExec() {
        return invoiceServiceMenu.hideCalculateLegacy();
    }


}
