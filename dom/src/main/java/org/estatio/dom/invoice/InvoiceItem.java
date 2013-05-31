package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.utils.Orderings;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public abstract class InvoiceItem extends EstatioTransactionalObject implements Comparable<InvoiceItem>, WithInterval {

    private Invoice invoice;

    @Render(Type.EAGERLY)
    @Disabled
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Title(sequence = "1", append = ":")
    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(final Invoice invoice) {
        this.invoice = invoice;
    }

    public void modifyInvoice(final Invoice invoice) {
        Invoice currentInvoice = getInvoice();
        if (invoice == null || invoice.equals(currentInvoice)) {
            return;
        }
        invoice.addToItems(this);
    }

    public void clearInvoice() {
        Invoice currentInvoice = getInvoice();
        if (currentInvoice == null) {
            return;
        }
        currentInvoice.removeFromItems(this);
    }

    
    
    
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

    
    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal quantity;

    @MemberOrder(sequence = "3")
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    
    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal netAmount;

    @MemberOrder(sequence = "4")
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal defaultNetAmount() {
        return BigDecimal.ZERO;
    }

    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal vatAmount;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "5")
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(final BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    
    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal grossAmount;

    @MemberOrder(sequence = "6")
    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    
    
    private Tax tax;

    @MemberOrder(sequence = "7")
    @Hidden(where = Where.PARENTED_TABLES)
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    private String description;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "8")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    
    @javax.jdo.annotations.Persistent
    private LocalDate dueDate;

    @MemberOrder(sequence = "9")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }


    // {{ StartDate, EndDate
    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @MemberOrder(sequence = "10")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    
    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(sequence = "11")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }
    
    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }
    // }}



    /**
     * Attaches this item to an invoice with similar attributes. Creates a new
     * invoice when no matching found.
     */
    public abstract void attachToInvoice();

    @Bulk
    public InvoiceItem verify() {
        calulateTax();
        return this;
    }

    @Hidden
    public void remove() {
        // no safeguard, assuming being called with precaution
        clearInvoice();
        getContainer().flush();
        getContainer().remove(this);
    }

    @Hidden
    private void calulateTax() {
        BigDecimal vatAmount = BigDecimal.ZERO;
        if (getTax() != null) {
            BigDecimal percentage = tax.percentageFor(getDueDate());
            if (percentage != null) {
                BigDecimal rate = percentage.divide(BigDecimal.valueOf(100));
                vatAmount = getNetAmount().multiply(rate).setScale(2, RoundingMode.HALF_UP);
            }
        }
        BigDecimal currentVatAmount = getVatAmount();
        if (currentVatAmount == null || vatAmount.compareTo(currentVatAmount) != 0) {
            setVatAmount(vatAmount);
            setGrossAmount(getNetAmount().add(vatAmount));
        }
    }

    public void created() {
        initialize();
    }

    @Hidden
    public void initialize() {
        // set defaults
        setVatAmount(BigDecimal.ZERO);
        setGrossAmount(BigDecimal.ZERO);
        setNetAmount(BigDecimal.ZERO);
    }


    // {{ Comparable impl
    @Override
    public int compareTo(InvoiceItem o) {
        return ORDERING_BY_INVOICE
                .compound(ORDERING_BY_START_DATE)
                .compound(ORDERING_BY_DUE_DATE)
                .compound(ORDERING_BY_DESC)
                .compare(this, o);
    }

    public static Ordering<InvoiceItem> ORDERING_BY_INVOICE = new Ordering<InvoiceItem>() {
        public int compare(InvoiceItem p, InvoiceItem q) {
            return Ordering.<Invoice> natural().nullsFirst().compare(p.getInvoice(), q.getInvoice());
        }
    };

    public final static Ordering<InvoiceItem> ORDERING_BY_START_DATE = new Ordering<InvoiceItem>() {
        public int compare(InvoiceItem p, InvoiceItem q) {
            return Orderings.LOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };

    public final static Ordering<InvoiceItem> ORDERING_BY_DUE_DATE = new Ordering<InvoiceItem>() {
        public int compare(InvoiceItem p, InvoiceItem q) {
            return Orderings.LOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getDueDate(), q.getDueDate());
        }
    };

    public static Ordering<InvoiceItem> ORDERING_BY_DESC = new Ordering<InvoiceItem>() {
        public int compare(InvoiceItem p, InvoiceItem q) {
            return Ordering.<String> natural().nullsFirst().compare(p.getDescription(), q.getDescription());
        }
    };

    // }}

    
    // {{ injected
    private Charges charges;
    public void injectChargesService(Charges charges) {
        this.charges = charges;
    }
    // }}

}