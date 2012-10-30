package com.eurocommercialproperties.estatio.dom.invoice;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.currency.Currency;

@PersistenceCapable
public class Invoice extends AbstractDomainObject {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ InvoiceDate (property)
    private LocalDate invoiceDate;

    @MemberOrder(sequence = "2")
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(final LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    // }}

    // {{ DueDate (property)
    private LocalDate dueDate;

    @MemberOrder(sequence = "3")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // }}

    // {{ Status (property)
    private InvoiceStatus status;

    @MemberOrder(sequence = "4")
    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(final InvoiceStatus status) {
        this.status = status;
    }

    // }}

    // {{ Currency (property)
    private Currency currency;

    @MemberOrder(sequence = "5")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }
    // }}

}
