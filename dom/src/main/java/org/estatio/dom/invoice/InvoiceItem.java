package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseActorType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;
import org.joda.time.LocalDate;

@PersistenceCapable
public class InvoiceItem extends AbstractDomainObject {

    // {{ Invoice (property)
    private Invoice invoice;

    @Disabled
    @MemberOrder(sequence = "1")
    @Hidden(where=Where.REFERENCES_PARENT)
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

    @MemberOrder(sequence = "1")
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

    @MemberOrder(sequence = "2")
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

    @MemberOrder(sequence = "3")
    @Column(scale = 4)
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    // }}

    // {{ VatAmount (property)
    private BigDecimal vatAmount;

    @MemberOrder(sequence = "4")
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

    @MemberOrder(sequence = "5")
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

    @MemberOrder(sequence = "6")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // }}

    // {{ Description (property)
    private String description;

    @MemberOrder(sequence = "7")
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
    @MemberOrder(sequence = "8")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    // }}
    
    // {{ StartDate (property)
    private LocalDate startDate;

    @MemberOrder(sequence = "9")
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

    @MemberOrder(sequence = "10")
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
    @Hidden(where=Where.REFERENCES_PARENT)
    @MemberOrder(sequence = "11")
    public LeaseTerm getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(final LeaseTerm leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public void modifyLeaseTerm(final LeaseTerm leaseTerm) {
        LeaseTerm currentLeaseTerm = getLeaseTerm();
        // check for no-op
        if (leaseTerm == null || leaseTerm.equals(currentLeaseTerm)) {
            return;
        }
        // delegate to parent to associate
        leaseTerm.addToInvoiceItems(this);
        // additional business logic
        onModifyLeaseTerm(currentLeaseTerm, leaseTerm);
    }

    public void clearLeaseTerm() {
        LeaseTerm currentLeaseTerm = getLeaseTerm();
        // check for no-op
        if (currentLeaseTerm == null) {
            return;
        }
        // delegate to parent to dissociate
        currentLeaseTerm.removeFromInvoiceItems(this);
        // additional business logic
        onClearLeaseTerm(currentLeaseTerm);
    }

    protected void onModifyLeaseTerm(final LeaseTerm oldLeaseTerm, final LeaseTerm newLeaseTerm) {
    }

    protected void onClearLeaseTerm(final LeaseTerm oldLeaseTerm) {
    }
    // }}

    public void findInvoice() {
        Lease lease = getLeaseTerm().getLeaseItem().getLease();
        if (lease != null) {
            Party seller = lease.findActorWithType(LeaseActorType.LANDLORD, getDueDate()).getParty();
            Party buyer = lease.findActorWithType(LeaseActorType.TENANT, getDueDate()).getParty();
            PaymentMethod paymentMethod = getLeaseTerm().getLeaseItem().getPayymentMethod();
            Invoice invoice = invoices.findMatchingInvoice(seller, buyer, paymentMethod, lease, InvoiceStatus.CONCEPT);
            if (invoice == null) {
                invoice = invoices.newInvoice();
                invoice.setBuyer(buyer);
                invoice.setSeller(seller);
                invoice.setLease(lease);
                invoice.setDueDate(getDueDate());
                invoice.setPaymentMethod(paymentMethod);
                invoice.setStatus(InvoiceStatus.CONCEPT);
            }
            invoice.addToItems(this);
            this.setInvoice(invoice);
        }
    }    
    
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
