package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.numerator.NumeratorForInvoiceNumber;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

@PersistenceCapable
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class Invoice extends EstatioTransactionalObject {

    public String title() {
        return String.format("%08d", Integer.parseInt(getId()));
    }

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

    // {{ NumeratorForInvoiceNumber (property)
    private String invoiceNumber;

    @MemberOrder(sequence = "3")
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    // }}

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "4")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Lease (property)
    private Lease lease;

    @MemberOrder(sequence = "5")
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // }}

    // {{ InvoiceDate (property)
    private LocalDate invoiceDate;

    @MemberOrder(sequence = "6")
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(final LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    // }}

    // {{ DueDate (property)
    private LocalDate dueDate;

    @MemberOrder(sequence = "7")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // }}

    // {{ Status (property)
    private InvoiceStatus status;

    @MemberOrder(sequence = "8")
    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(final InvoiceStatus status) {
        this.status = status;
    }

    // }}

    // {{ Currency (property)
    private Currency currency;

    @MemberOrder(sequence = "9")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    // }}

    // {{ PaymentMethod (property)
    private PaymentMethod paymentMethod;

    @MemberOrder(sequence = "10")
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // }}

    // {{ Items (Collection)
    private Set<InvoiceItem> items = new LinkedHashSet<InvoiceItem>();

    @MemberOrder(sequence = "11")
    @Render(Type.EAGERLY)
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

    @MemberOrder(sequence = "12")
    public BigDecimal getNetAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getNetAmount());
        }
        return total;
    }

    @MemberOrder(sequence = "13")
    public BigDecimal getVatAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getVatAmount());
        }
        return total;
    }

    @MemberOrder(sequence = "14")
    public BigDecimal getGrossAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getGrossAmount());
        }
        return total;
    }

    // {{ Actions

    @Bulk
    @MemberOrder(sequence = "20")
    public Invoice assignInvoiceNumber() {
        NumeratorForInvoiceNumber numerator = (NumeratorForInvoiceNumber) numerators.find(NumeratorType.INVOICE_NUMBER);
        if (numerator.assign(this)) {
            informUser("Assigned " + this.getInvoiceNumber() + " to invoice " + getContainer().titleOf(this));
        }
        return this;
    }

    // }}

    // {{ Inject services

    private Parties parties;

    public void setParties(Parties parties) {
        this.parties = parties;
    }

    private Numerators numerators;

    public void setNumerators(Numerators numerators) {
        this.numerators = numerators;
    }
    // }}

}
