package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

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

    @Persistent
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

    @Persistent
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
    @Persistent(mappedBy = "invoice")
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
        // dissociate arg from its current parent (if any).
        item.clearInvoice();
        // associate arg
        item.setInvoice(this);
        getItems().add(item);
    }

    public void removeFromItems(final InvoiceItem item) {
        // check for no-op
        if (item == null || !getItems().contains(item)) {
            return;
        }
        // dissociate arg
        item.setInvoice(null);
        getItems().remove(item);
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
    public Invoice approve() {
        this.setStatus(InvoiceStatus.APPROVED);
        return this;
    }

    @Bulk
    @MemberOrder(sequence = "21")
    public Invoice assignCollectionNumber() {
        Numerator numerator = numerators.establish(NumeratorType.COLLECTION_NUMBER);
        if (assign(this, numerator, "COL-%05d")) {
            informUser("Assigned " + this.getInvoiceNumber() + " to invoice " + getContainer().titleOf(this));
            this.setStatus(InvoiceStatus.COLLECTED);
        }
        return this;
    }

    @Bulk
    @MemberOrder(sequence = "22")
    public Invoice assignInvoiceNumber() {
        Numerator numerator = numerators.establish(NumeratorType.INVOICE_NUMBER);
        if (assign(this, numerator, "INV-%05d")) {
            informUser("Assigned " + this.getInvoiceNumber() + " to invoice " + getContainer().titleOf(this));
            this.setStatus(InvoiceStatus.INVOICED);
        }
        return this;
    }

    private static boolean assign(Invoice invoice, Numerator numerator, String format) {
        if (invoice.getInvoiceNumber() != null) {
            return false;
        }
        invoice.setInvoiceNumber(String.format(format, numerator.increment()));
        return true;
    }

    // TODO: Prototype and Bulk don't seem to go well together
    //@Prototype
    @Bulk
    public void remove() {
//        if (getStatus().equals(InvoiceStatus.NEW)) {
            for (InvoiceItem item : getItems()) {
                item.remove();
            }
            getContainer().remove(this);
//        }
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
