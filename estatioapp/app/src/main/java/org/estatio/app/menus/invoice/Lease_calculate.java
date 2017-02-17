package org.estatio.app.menus.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.invoice.InvoiceRunType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;

@Mixin(method = "exec")
public class Lease_calculate {

    private final Lease lease;

    public Lease_calculate(final Lease lease) {
        this.lease = lease;
    }

    @Inject
    InvoiceServiceMenu invoiceServiceMenu;


    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public Object exec(
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        return invoiceServiceMenu.calculate(lease, runType, leaseItemTypes, invoiceDueDate, startDueDate, nextDueDate);
    }

    public List<LeaseItemType> choices1Exec() {
        return invoiceServiceMenu.choices2Calculate();
    }

    public InvoiceRunType default0Exec() {
        return invoiceServiceMenu.default1Calculate();
    }

    public List<LeaseItemType> default1Exec() {
        return invoiceServiceMenu.default2Calculate();
    }

    public LocalDate default2Exec() {
        return invoiceServiceMenu.default3Calculate();
    }

    public LocalDate default3Exec() {
        return invoiceServiceMenu.default4Calculate();
    }

    public LocalDate default4Exec() {
        return invoiceServiceMenu.default5Calculate();
    }

    public String validateExec(
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return invoiceServiceMenu.validateCalculate(lease, runType, leaseItemTypes, dueDate, startDate, endDate);
    }

    public String disableExec(){
        return invoiceServiceMenu.doDisableCalculate(lease);
    }

    public boolean hideExec() {
        return invoiceServiceMenu.hideCalculate();
    }

}
