package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.PaymentMethodType;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable
public class Invoice extends AbstractDomainObject {

    // {{ Buyer (property)
    private Party buyer;

    @MemberOrder(sequence = "1")
    public Party getBuyer() {
        return buyer;
    }

    public void setBuyer(final Party buyer) {
        this.buyer = buyer;
    }

    public List<Party> choicesBuyer() {
        return parties.allParties();
    }

    // }}

    // {{ Seller (property)
    private Party seller;

    @MemberOrder(sequence = "2")
    public Party getSeller() {
        return seller;
    }

    public void setSeller(final Party seller) {
        this.seller = seller;
    }

    public List<Party> choicesSeller() {
        return parties.allParties();
    }
    // }}

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "3")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ InvoiceDate (property)
    private LocalDate invoiceDate;

    @MemberOrder(sequence = "4")
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(final LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    // }}

    // {{ DueDate (property)
    private LocalDate dueDate;

    @MemberOrder(sequence = "5")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // }}

    // {{ Status (property)
    private InvoiceStatus status;

    @MemberOrder(sequence = "6")
    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(final InvoiceStatus status) {
        this.status = status;
    }

    // }}

    // {{ Currency (property)
    private Currency currency;

    @MemberOrder(sequence = "7")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    // }}

    // {{ PaymentMethod (property)
    private PaymentMethodType paymentMethod;

    @MemberOrder(sequence = "8")
    public PaymentMethodType getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethodType paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    // }}

    // {{ Items (Collection)
    private Set<InvoiceItem> items = new LinkedHashSet<InvoiceItem>();

    @MemberOrder(sequence = "9")
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

    // {{ Inject services

    private Parties parties;

    public void setParties(Parties parties) {
        this.parties = parties;
    }

    // }}

}
