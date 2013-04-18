package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseActorType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;
import org.joda.time.LocalDate;

@PersistenceCapable
// REVIEW: is one needed?
// @DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column =
// "INVOICE_ITEM_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class InvoiceItem extends EstatioTransactionalObject {

    // {{ Invoice (property)
    private Invoice invoice;

    @Disabled
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Title(sequence = "1", append = ":")
    @Optional
    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(final Invoice invoice) {
        this.invoice = invoice;
    }

    // }}

    // {{ Charge (property)
    private Charge charge;

    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public Charge getCharge() {
        return charge;
    }

    public void setCharge(final Charge charge) {
        this.charge = charge;
    }

    public List<Charge> choicesCharge() {
        return charges.allCharges();

    }

    // }}

    // {{ Quantity (property)
    private BigDecimal quantity;

    @MemberOrder(sequence = "3")
    @Column(scale = 4)
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    // }}

    // {{ NetAmount (property)
    private BigDecimal netAmount;

    @MemberOrder(sequence = "4")
    @Column(scale = 4)
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal defaultNetAmount() {
        return BigDecimal.ZERO;
    }

    // }}

    // {{ VatAmount (property)
    private BigDecimal vatAmount;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "5")
    @Column(scale = 4)
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(final BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    // }}

    // {{ Amount (property)
    private BigDecimal grossAmount;

    @MemberOrder(sequence = "6")
    @Column(scale = 4)
    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    // }}

    // {{ Tax (property)
    private Tax tax;

    @MemberOrder(sequence = "7")
    @Hidden(where = Where.PARENTED_TABLES)
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // }}

    // {{ Description (property)
    private String description;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "8")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // }}

    // {{ DueDate (property)
    private LocalDate dueDate;

    @Persistent
    @MemberOrder(sequence = "9")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @MemberOrder(sequence = "10")
    @Persistent
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @MemberOrder(sequence = "11")
    @Persistent
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // }}

    // {{ LeaseTerm (property)
    private LeaseTerm leaseTerm;

    @Disabled
    @Hidden(where = Where.REFERENCES_PARENT)
    @MemberOrder(sequence = "11")
    public LeaseTerm getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(final LeaseTerm leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    // }}

    // {{ Actions
    /**
     * Attaches this item to an invoice with similar attributes. Creates a new
     * invoice when no matching found.
     */
    @Hidden
    public void attachToInvoice() {
        Lease lease = getLeaseTerm().getLeaseItem().getLease();
        if (lease != null) {
            resolve(lease);
            Party seller = lease.findActorWithType(LeaseActorType.LANDLORD, getDueDate()).getParty();
            Party buyer = lease.findActorWithType(LeaseActorType.TENANT, getDueDate()).getParty();
            PaymentMethod paymentMethod = getLeaseTerm().getLeaseItem().getPayymentMethod();
            Invoice invoice = invoices.findMatchingInvoice(seller, buyer, paymentMethod, lease, InvoiceStatus.NEW, getDueDate());
            if (invoice == null) {
                invoice = invoices.newInvoice();
                invoice.setBuyer(buyer);
                invoice.setSeller(seller);
                invoice.setLease(lease);
                invoice.setDueDate(getDueDate());
                invoice.setPaymentMethod(paymentMethod);
                invoice.setStatus(InvoiceStatus.NEW);
            }
            this.setInvoice(invoice);
        }
    }

    @Bulk
    public InvoiceItem verify() {
        calulateTax();
        return this;
    }
    
    @Hidden 
    public void remove() {
        // no safeguard, assuming being called with precaution
        setInvoice(null);
        setLeaseTerm(null);
        getContainer().flush();
        getContainer().remove(this);
    }

    @Hidden
    private void calulateTax() {
        BigDecimal vatAmount = BigDecimal.ZERO;
        if (getTax() != null) {
            BigDecimal rate = tax.percentageFor(getDueDate()).divide(BigDecimal.valueOf(100));
            vatAmount = getNetAmount().multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal currentVatAmount = getVatAmount();
        if (currentVatAmount == null || vatAmount.compareTo(currentVatAmount) != 0) {
            setVatAmount(vatAmount);
            setGrossAmount(getNetAmount().add(vatAmount));
        }
    }

    // }

    // {{ Lifecycle Events
    public void created() {
        // set defaults
        setVatAmount(BigDecimal.ZERO);
        setGrossAmount(BigDecimal.ZERO);
        setNetAmount(BigDecimal.ZERO);
    }

    // }}

    // {{ Inject services

    private Charges charges;

    @Hidden
    public void setChargesService(Charges charges) {
        this.charges = charges;
    }

    private Invoices invoices;

    @Hidden
    public void setInvoices(Invoices invoices) {
        this.invoices = invoices;
    }

    // }}

}
