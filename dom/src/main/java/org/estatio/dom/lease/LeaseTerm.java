package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoicesForLease;
import org.estatio.dom.utils.Orderings;
import org.estatio.dom.utils.ValueUtils;
import org.estatio.services.clock.ClockService;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="iid"),
        @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="4"),
    })*/
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "LEASETERM_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class LeaseTerm extends EstatioTransactionalObject implements Comparable<LeaseTerm> {

    
    @javax.jdo.annotations.Persistent
    private LeaseItem leaseItem;

    @Hidden(where = Where.REFERENCES_PARENT)
    @MemberOrder(sequence = "1")
    @Disabled
    @Title(sequence = "1", append = ":")
    public LeaseItem getLeaseItem() {
        return leaseItem;
    }

    public void setLeaseItem(final LeaseItem leaseItem) {
        this.leaseItem = leaseItem;
    }

    public void modifyLeaseItem(final LeaseItem item) {
        LeaseItem currentLeaseItem = getLeaseItem();
        // check for no-op
        if (item == null || item.equals(currentLeaseItem)) {
            return;
        }
        // delegate to parent to associate
        item.addToTerms(this);
    }

    public void clearLeaseItem() {
        LeaseItem currentLeaseItem = getLeaseItem();
        // check for no-op
        if (currentLeaseItem == null) {
            return;
        }
        // delegate to parent to dissociate
        currentLeaseItem.removeFromTerms(this);
    }

    private BigInteger sequence;

    @Hidden
    @Optional
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    
    
    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Title(sequence = "2", append = "-")
    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    
    
    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(sequence = "3")
    @Title(sequence = "3")
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal value;

    @MemberOrder(sequence = "4")
    @Mask("")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    private LeaseTermStatus status;

    @Disabled
    // maintained through LeaseTermContributedActions
    @MemberOrder(sequence = "5")
    public LeaseTermStatus getStatus() {
        return status;
    }

    public void setStatus(final LeaseTermStatus status) {
        this.status = status;
    }

    
    
    private LeaseTermFrequency frequency;

    @MemberOrder(sequence = "6")
    public LeaseTermFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(final LeaseTermFrequency frequency) {
        this.frequency = frequency;
    }

    
    
    @javax.jdo.annotations.Persistent(mappedBy = "nextTerm")
    private LeaseTerm previousTerm;

    @MemberOrder(sequence = "6")
    @Hidden
    @Optional
    public LeaseTerm getPreviousTerm() {
        return previousTerm;
    }

    public void setPreviousTerm(final LeaseTerm previousTerm) {
        this.previousTerm = previousTerm;
    }

    
    
    private LeaseTerm nextTerm;

    @MemberOrder(sequence = "7")
    @Hidden
    @Optional
    public LeaseTerm getNextTerm() {
        return nextTerm;
    }

    public void setNextTerm(final LeaseTerm nextTerm) {
        this.nextTerm = nextTerm;
    }

    public void modifyNextTerm(LeaseTerm term) {
        this.setNextTerm(term);
        term.setPreviousTerm(this);
    }

    public void clearNextTerm() {
        LeaseTerm nextTerm = getNextTerm();
        if (nextTerm != null) {
            nextTerm.setPreviousTerm(null);
            setNextTerm(null);
        }
    }

    @Persistent(mappedBy = "leaseTerm")
    private Set<InvoiceItemForLease> invoiceItems = new LinkedHashSet<InvoiceItemForLease>();

    @MemberOrder(sequence = "1")
    @Render(Type.EAGERLY)
    public Set<InvoiceItemForLease> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(final Set<InvoiceItemForLease> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public void addToInvoiceItems(final InvoiceItemForLease invoiceItem) {
        // check for no-op
        if (invoiceItem == null || getInvoiceItems().contains(invoiceItem)) {
            return;
        }
        // dissociate arg from its current parent (if any).
        invoiceItem.clearLeaseTerm();
        // associate arg
        invoiceItem.setLeaseTerm(this);
        getInvoiceItems().add(invoiceItem);
    }

    public void removeFromInvoiceItems(final InvoiceItemForLease invoiceItem) {
        // check for no-op
        if (invoiceItem == null || !getInvoiceItems().contains(invoiceItem)) {
            return;
        }
        // dissociate arg
        invoiceItem.setLeaseTerm(null);
        getInvoiceItems().remove(invoiceItem);
    }

    @Hidden
    public void removeUnapprovedInvoiceItemsForDate(LocalDate startDate, LocalDate dueDate) {
        for (InvoiceItemForLease invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW)) && startDate.equals(invoiceItem.getStartDate()) && dueDate.equals(invoiceItem.getDueDate())) {
                invoiceItem.clearInvoice();
                invoiceItem.clearLeaseTerm();
                getContainer().flush();
                remove(invoiceItem);
            }
        }
    }

    @Hidden
    public InvoiceItemForLease findOrCreateUnapprovedInvoiceItemFor(LocalDate startDate, LocalDate dueDate) {
        InvoiceItemForLease ii = findUnapprovedInvoiceItemFor(startDate, dueDate);
        if (ii == null) {
            ii = invoices.newInvoiceItem();
            ii.modifyLeaseTerm(this);
        }
        return ii;
    }

    @Hidden
    public InvoiceItemForLease findUnapprovedInvoiceItemFor(LocalDate startDate, LocalDate dueDate) {
        for (InvoiceItemForLease invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW)) && startDate.equals(invoiceItem.getStartDate()) && dueDate.equals(invoiceItem.getDueDate())) {
                return invoiceItem;
            }
        }
        return null;
    }

    @Hidden
    public BigDecimal invoicedValueFor(LocalDate startDate) {
        BigDecimal invoicedValue = new BigDecimal(0);
        for (InvoiceItemForLease invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if (invoice == null || invoice.getStatus() == InvoiceStatus.NEW || invoiceItem.getStartDate() == null || invoiceItem.getStartDate().compareTo(startDate) != 0) {
                continue;
            }
            invoicedValue = invoicedValue.add(invoiceItem.getNetAmount());
        }
        return invoicedValue;
    }

    @MemberOrder(name = "invoiceItems", sequence = "2")
    public LeaseTerm calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due Date") LocalDate dueDate) {
        if (getStatus() == LeaseTermStatus.APPROVED) {
            invoiceCalculationService.calculateAndInvoiceItems(this, startDate, dueDate, getLeaseItem().getInvoicingFrequency());
            informUser("Calculated" + this.getLeaseItem().getLease().getReference());
            // TODO: use the title of this term? But how access it.
        }
        return this;
    }

    // {{ Actions
    @MemberOrder(sequence = "1")
    public LeaseTerm approve() {
        setStatus(LeaseTermStatus.APPROVED);
        return this;
    }

    public String disableApprove() {
        return this.getStatus() == LeaseTermStatus.NEW ? null : "Cannot approve. Already approved?";
    }

    @MemberOrder(sequence = "2")
    public LeaseTerm verify() {
        update();

        // convenience code to automatically create terms but not for terms who
        // have a start date after today
        if (getStartDate() != null && getStartDate().compareTo(clockService.now()) < 0) {
            createNext();
            nextTerm = getNextTerm();
            if (nextTerm != null) {
                nextTerm.verify();
            }
        }
        return this;
    }

    @MemberOrder(sequence = "3")
    public LeaseTerm createNext() {
        LocalDate newStartDate = getEndDate() == null ? this.getFrequency().nextDate(this.getStartDate()) : this.getEndDate().plusDays(1);
        return createNext(newStartDate);
    }

    LeaseTerm createNext(LocalDate nextStartDate) {
        if (getNextTerm() != null) {
            return null;
        } 
        
        final LocalDate endDate = getLeaseItem().calculatedEndDate();
        final LocalDate oneYearFromNow = clockService.now().plusYears(1);
        final LocalDate maxEndDate = ValueUtils.coalesce(endDate, oneYearFromNow);
        if (nextStartDate.isAfter(maxEndDate)) {
            // date is after end date, do nothing
            return null;
        }
        
        LeaseTerm term = getNextTerm();
        if (getNextTerm() == null) {
            term = getLeaseItem().createNextTerm(this);
        }
        
        // new start Date
        term.setStartDate(nextStartDate);
        term.update();
        this.setEndDate(nextStartDate.minusDays(1));
        return term;
    }

    protected void initialize() {
        setStatus(LeaseTermStatus.NEW);
        final BigInteger previousTermSequence = previousTermSequence().add(BigInteger.ONE);
        setSequence(previousTermSequence);
    }

    private BigInteger previousTermSequence() {
        return getPreviousTerm() != null 
            ? getPreviousTerm().getSequence() 
            : BigInteger.ZERO;
    }

    protected void update() {
        // check endDate and startDate relationship
        final LeaseTerm nextTerm = getNextTerm();
        if (nextTerm != null && nextTerm.getStartDate() != null && (getEndDate() == null || nextTerm.getStartDate().compareTo(getEndDate().plusDays(1)) != 1)) {
            setEndDate(nextTerm.getStartDate().minusDays(1));
        }
    }

    @Hidden
    public BigDecimal valueForDueDate(LocalDate dueDate) {
        return getValue();
    }

    @Hidden BigDecimal valueForPeriod(InvoicingFrequency frequency, LocalDate periodStartDate, LocalDate dueDate) {
        if (getStatus() == LeaseTermStatus.APPROVED) {
            BigDecimal value = invoiceCalculationService.calculatedValue(this, periodStartDate, dueDate, frequency);
            return value;
        }
        return BigDecimal.ZERO;
    }

    // {{ CompareTo
    @Override
    @Hidden
    public int compareTo(LeaseTerm o) {
        return ORDERING_BY_CLASS.compound(ORDERING_BY_START_DATE).compare(this, o);
    }

    public static Ordering<LeaseTerm> ORDERING_BY_CLASS = new Ordering<LeaseTerm>() {
        public int compare(LeaseTerm p, LeaseTerm q) {
            return Ordering.<String> natural().compare(p.getClass().toString(), q.getClass().toString());
        }
    };

    public final static Ordering<LeaseTerm> ORDERING_BY_START_DATE = new Ordering<LeaseTerm>() {
        public int compare(LeaseTerm p, LeaseTerm q) {
            return Orderings.LOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };

    // }}

    // {{ Injected services
    private InvoicesForLease invoices;
    public void injectInvoices(InvoicesForLease invoices) {
        this.invoices = invoices;
    }

    private InvoiceCalculationService invoiceCalculationService;
    public void injectInvoiceCalculationService(InvoiceCalculationService invoiceCalculationService) {
        this.invoiceCalculationService = invoiceCalculationService;
    }

    private ClockService clockService;
    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }
    // }}

}
