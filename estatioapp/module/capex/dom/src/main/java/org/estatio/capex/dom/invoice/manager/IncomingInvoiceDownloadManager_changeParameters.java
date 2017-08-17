package org.estatio.capex.dom.invoice.manager;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.dom.asset.Property;

//region > changeParameters (action)
@Mixin(method="act")
public class IncomingInvoiceDownloadManager_changeParameters {
    private final IncomingInvoiceDownloadManager manager;
    public IncomingInvoiceDownloadManager_changeParameters(final IncomingInvoiceDownloadManager manager) {
        this.manager = manager;
    }
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceDownloadManager act(
            final LocalDate fromInputDate,
            final LocalDate toInputDate,
            @Nullable
            final org.estatio.dom.asset.Property property,
            @Nullable
            final IncomingInvoiceType incomingInvoiceType){
        manager.setFromInputDate(fromInputDate);
        manager.setToInputDate(toInputDate);
        manager.setPropertyReference(property == null ? null : property.getReference());
        manager.setIncomingInvoiceTypeName(incomingInvoiceType == null ? null : incomingInvoiceType.name());
        return new IncomingInvoiceDownloadManager(fromInputDate, toInputDate, property, incomingInvoiceType);
    }

    public LocalDate default0Act() {
        return manager.getFromInputDate();
    }

    public LocalDate default1Act() {
        return manager.getToInputDate();
    }

    public Property default2Act() {
        return manager.getProperty();
    }

    public IncomingInvoiceType default3Act() {
        return manager.getIncomingInvoiceType();
    }

}
