package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.estatio.dom.currency.Currency;
import org.joda.time.LocalDate;

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

    // {{ Items (Collection)
    private Set<InvoiceItem> items = new LinkedHashSet<InvoiceItem>();

    @MemberOrder(sequence = "6")
    public Set<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(final Set<InvoiceItem> items) {
        this.items = items;
    }

    public void addToItems(final InvoiceItem item) {
        // check for no-op
        if (item == null || getItems().contains(item)) {
            return;
        }
        // associate new
        getItems().add(item);
        // additional business logic
        onAddToItems(item);
    }

    public void removeFromItems(final InvoiceItem item) {
        // check for no-op
        if (item == null || !getItems().contains(item)) {
            return;
        }
        // dissociate existing
        getItems().remove(item);
        // additional business logic
        onRemoveFromItems(item);
    }

    protected void onAddToItems(final InvoiceItem item) {
    }

    protected void onRemoveFromItems(final InvoiceItem item) {
    }

    // }}

    @MemberOrder(sequence = "10")
    public BigDecimal getNetAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getNetAmount());
        }
        return total;
    }

    @MemberOrder(sequence = "11")
    public BigDecimal getVatAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getVatAmount());
        }
        return total;
    }

    @MemberOrder(sequence = "12")
    public BigDecimal getGrossAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getGrossAmount());
        }
        return total;
    }

    // TODO: Add aggreated fields for NetAmount, VatAmount and GrossAmount
    // amount

}
