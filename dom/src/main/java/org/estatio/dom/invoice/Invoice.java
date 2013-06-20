package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.PublishedAction;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.publishing.InvoiceEagerlyRenderedPayloadFactory;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({ @javax.jdo.annotations.Query(name = "invoice_findMatchingInvoices", language = "JDOQL", value = "SELECT FROM org.estatio.dom.invoice.Invoice WHERE provenance == :provenance && seller == :seller && buyer == :buyer && paymentMethod == :paymentMethod && status == :status && dueDate == :dueDate") })
@Bookmarkable
public class Invoice extends EstatioTransactionalObject<Invoice> implements /*Comparable<Invoice>, */ WithReferenceGetter {


    public Invoice() {
        super("invoiceNumber");
    }
    
    // //////////////////////////////////////

    public String title() {
        return String.format("%08d", Integer.parseInt(getId()));
    }

    // //////////////////////////////////////

    private Party buyer;

    @MemberOrder(sequence = "1")
    public Party getBuyer() {
        return buyer;
    }

    public void setBuyer(final Party buyer) {
        this.buyer = buyer;
    }

    // //////////////////////////////////////

    private Party seller;

    @MemberOrder(sequence = "2")
    public Party getSeller() {
        return seller;
    }

    public void setSeller(final Party seller) {
        this.seller = seller;
    }

    // //////////////////////////////////////

    private String collectionNumber;

    @MemberOrder(sequence = "3")
    public String getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(String collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    // //////////////////////////////////////

    private String invoiceNumber;

    @MemberOrder(sequence = "4")
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    // //////////////////////////////////////

    private String reference;

    @MemberOrder(sequence = "5")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(extensions = { @Extension(vendorName = "datanucleus", key = "mapping-strategy", value = "per-implementation") })
    private InvoiceProvenance provenance;

    @MemberOrder(sequence = "6")
    public InvoiceProvenance getProvenance() {
        return provenance;
    }

    public void setProvenance(final InvoiceProvenance provenance) {
        this.provenance = provenance;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate invoiceDate;

    @Disabled
    @MemberOrder(sequence = "7")
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(final LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate dueDate;

    @MemberOrder(sequence = "8")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // //////////////////////////////////////

    private InvoiceStatus status;

    @Disabled
    @MemberOrder(sequence = "9")
    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(final InvoiceStatus status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private Currency currency;

    @MemberOrder(sequence = "10")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    // //////////////////////////////////////

    private PaymentMethod paymentMethod;

    @MemberOrder(sequence = "11")
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "invoice")
    private SortedSet<InvoiceItem> items = new TreeSet<InvoiceItem>();

    @MemberOrder(sequence = "12")
    @Render(Type.EAGERLY)
    public SortedSet<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(final SortedSet<InvoiceItem> items) {
        this.items = items;
    }

    public void addToItems(final InvoiceItem item) {
        if (item == null || getItems().contains(item)) {
            return;
        }
        item.clearInvoice();
        item.setInvoice(this);
        getItems().add(item);
    }

    public void removeFromItems(final InvoiceItem item) {
        if (item == null || !getItems().contains(item)) {
            return;
        }
        item.setInvoice(null);
        getItems().remove(item);
    }

    // //////////////////////////////////////

    @Persistent
    private BigInteger lastItemSequence;

    @Hidden
    public BigInteger getLastItemSequence() {
        return lastItemSequence;
    }

    public void setLastItemSequence(BigInteger lastItemSequence) {
        this.lastItemSequence = lastItemSequence;
    }

    public BigInteger nextItemSequence() {
        BigInteger nextItemSequence = getLastItemSequence() == null ? BigInteger.ONE : getLastItemSequence().add(BigInteger.ONE);
        setLastItemSequence(nextItemSequence);
        return nextItemSequence;
    }

    // //////////////////////////////////////

    @NotPersisted
    @MemberOrder(name = "Amounts", sequence = "13")
    public BigDecimal getNetAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getNetAmount());
        }
        return total;
    }

    @NotPersisted
    @MemberOrder(name = "Amounts", sequence = "14")
    public BigDecimal getVatAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getVatAmount());
        }
        return total;
    }

    @NotPersisted
    @MemberOrder(name = "Amounts", sequence = "15")
    public BigDecimal getGrossAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getGrossAmount());
        }
        return total;
    }

    // //////////////////////////////////////

    @Bulk
    @MemberOrder(sequence = "1")
    public Invoice approve() {
        this.setStatus(InvoiceStatus.APPROVED);
        return this;
    }

    @Bulk
    @MemberOrder(sequence = "2")
    public Invoice assignCollectionNumber() {
        Numerator numerator = numerators.establish(NumeratorType.COLLECTION_NUMBER);
        if (getInvoiceNumber() != null) {
            return null;
        }
        setInvoiceNumber(String.format("COL-%05d", numerator.increment()));
        informUser("Assigned " + this.getCollectionNumber() + " to invoice " + getContainer().titleOf(this));
        this.setStatus(InvoiceStatus.COLLECTED);
        return this;
    }

    @PublishedAction(InvoiceEagerlyRenderedPayloadFactory.class)
    @Bulk
    @MemberOrder(sequence = "3")
    public Invoice submitToCoda() {
        assignCollectionNumber();
        return this;
    }

    @Bulk
    @MemberOrder(sequence = "4")
    public Invoice assignInvoiceNumber() {
        Numerator numerator = numerators.establish(NumeratorType.INVOICE_NUMBER);
        if (getInvoiceNumber() != null) {
            return null;
        }
        setInvoiceNumber(String.format("INV-%05d", numerator.increment()));
        informUser("Assigned " + this.getInvoiceNumber() + " to invoice " + getContainer().titleOf(this));
        this.setStatus(InvoiceStatus.INVOICED);
        return this;
    }

    // //////////////////////////////////////

    @Prototype
    @Bulk
    @MemberOrder(sequence = "5")
    public void remove() {
        for (InvoiceItem item : getItems()) {
            item.remove();
        }
        getContainer().remove(this);
    }

    // //////////////////////////////////////


    private Numerators numerators;

    public void injectNumerators(Numerators numerators) {
        this.numerators = numerators;
    }

}
