package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Objects;
import com.google.common.collect.Ordering;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithSequence;
import org.estatio.dom.WithStartDate;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoicesForLease;
import org.estatio.dom.utils.ValueUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "LEASETERM_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Indices({ @javax.jdo.annotations.Index(name = "LEASE_TERM_IDX", members = { "leaseItem", "sequence" }), @javax.jdo.annotations.Index(name = "LEASE_TERM2_IDX", members = { "leaseItem", "startDate" }) })
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(name="leaseTerm_findLeaseTermsWithStatus",language="JDOQL", value="SELECT FROM org.estatio.dom.lease.LeaseTerm WHERE status == :status && startDate <= :date && (endDate == null || endDate >= :date)"),
    @javax.jdo.annotations.Query(name="leaseTerm_findLeaseTermsWithSequence", language="JDOQL", value = "SELECT FROM org.estatio.dom.lease.LeaseTerm WHERE leaseItem == :leaseItem && sequence == :sequence")
})


@Bookmarkable(BookmarkPolicy.AS_CHILD)
public abstract class LeaseTerm extends EstatioTransactionalObject implements Comparable<LeaseTerm>, WithInterval, WithSequence {

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
        if (item == null || item.equals(currentLeaseItem)) {
            return;
        }
        item.addToTerms(this);
    }

    public void clearLeaseItem() {
        LeaseItem currentLeaseItem = getLeaseItem();
        if (currentLeaseItem == null) {
            return;
        }
        currentLeaseItem.removeFromTerms(this);
    }

    // //////////////////////////////////////

    private BigInteger sequence;

    @Hidden
    @Optional
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    // //////////////////////////////////////

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

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    private LeaseTermStatus status;

    /**
     * Disabled, is maintained through LeaseTermContributedActions
     */
    @Disabled
    @MemberOrder(sequence = "4")
    public LeaseTermStatus getStatus() {
        return status;
    }

    public void setStatus(final LeaseTermStatus status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private LeaseTermFrequency frequency;

    @MemberOrder(sequence = "5")
    public LeaseTermFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(final LeaseTermFrequency frequency) {
        this.frequency = frequency;
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "6", name = "Values")
    @Mask("")
    public abstract BigDecimal getTrialValue();

    // //////////////////////////////////////

    @MemberOrder(sequence = "7", name = "Values")
    @Mask("")
    public abstract BigDecimal getApprovedValue();

    // //////////////////////////////////////

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

    public void modifyPreviousTerm(final LeaseTerm previousTerm) {
        LeaseTerm currentPreviousTerm = getPreviousTerm();
        if (previousTerm == null || previousTerm.equals(currentPreviousTerm)) {
            return;
        }
        clearPreviousTerm();
        previousTerm.setNextTerm(this);
        setPreviousTerm(previousTerm);
    }

    public void clearPreviousTerm() {
        LeaseTerm currentPreviousTerm = getPreviousTerm();
        if (currentPreviousTerm == null) {
            return;
        }
        currentPreviousTerm.setNextTerm(null);
        setPreviousTerm(null);
    }

    // //////////////////////////////////////

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

    public void modifyNextTerm(final LeaseTerm nextTerm) {
        LeaseTerm currentNextTerm = getNextTerm();
        if (nextTerm == null || nextTerm.equals(currentNextTerm)) {
            return;
        }
        if (currentNextTerm != null) {
            currentNextTerm.clearPreviousTerm();
        }
        nextTerm.modifyPreviousTerm(this);
    }

    public void clearNextTerm() {
        LeaseTerm currentNextTerm = getNextTerm();
        if (currentNextTerm == null) {
            return;
        }
        currentNextTerm.clearPreviousTerm();
    }

    // //////////////////////////////////////

    @Persistent(mappedBy = "leaseTerm")
    private SortedSet<InvoiceItemForLease> invoiceItems = new TreeSet<InvoiceItemForLease>();

    @MemberOrder(sequence = "1")
    @Render(Type.EAGERLY)
    public SortedSet<InvoiceItemForLease> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(final SortedSet<InvoiceItemForLease> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public void addToInvoiceItems(final InvoiceItemForLease invoiceItem) {
        if (invoiceItem == null || getInvoiceItems().contains(invoiceItem)) {
            return;
        }
        invoiceItem.clearLeaseTerm();
        invoiceItem.setLeaseTerm(this);
        getInvoiceItems().add(invoiceItem);
    }

    public void removeFromInvoiceItems(final InvoiceItemForLease invoiceItem) {
        if (invoiceItem == null || !getInvoiceItems().contains(invoiceItem)) {
            return;
        }
        invoiceItem.setLeaseTerm(null);
        getInvoiceItems().remove(invoiceItem);
    }

    // //////////////////////////////////////

    @Programmatic
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

    @Programmatic
    public InvoiceItemForLease findOrCreateUnapprovedInvoiceItemFor(LocalDate startDate, LocalDate dueDate) {
        InvoiceItemForLease ii = findUnapprovedInvoiceItemFor(startDate, dueDate);
        if (ii == null) {
            ii = invoices.newInvoiceItem();
            ii.modifyLeaseTerm(this);
        }
        return ii;
    }

    @Programmatic
    public InvoiceItemForLease findUnapprovedInvoiceItemFor(LocalDate startDate, LocalDate dueDate) {
        for (InvoiceItemForLease invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW)) && startDate.equals(invoiceItem.getStartDate()) && dueDate.equals(invoiceItem.getDueDate())) {
                return invoiceItem;
            }
        }
        return null;
    }

    @Programmatic
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

    // //////////////////////////////////////
    
    @Deprecated
    @Hidden
    public LeaseTerm calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due Date") LocalDate dueDate) {
        return calculate(startDate, dueDate, false);
    }
    
    @MemberOrder(name = "invoiceItems", sequence = "2")
    public LeaseTerm calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due Date") LocalDate dueDate, @Named("Retro Run") boolean retroRun) {
        if (getStatus() == LeaseTermStatus.APPROVED) {
            invoiceCalculationService.calculateAndInvoice(this, startDate, dueDate, getLeaseItem().getInvoicingFrequency(), retroRun);
        }
        return this;
    }

    // //////////////////////////////////////

    @Bulk
    @MemberOrder(sequence = "1")
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

    // //////////////////////////////////////

    @Bulk
    @MemberOrder(sequence = "2")
    public LeaseTerm approve() {
        // guard against invalid updates when called as bulk action
        if (getStatus() == LeaseTermStatus.NEW) {
            setStatus(LeaseTermStatus.APPROVED);
        }
        return this;
    }

    public String disableApprove() {
        return this.getStatus() == LeaseTermStatus.NEW ? null : "Cannot approve. Already approved?";
    }

    // //////////////////////////////////////

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

    // //////////////////////////////////////

    protected void initialize() {
        setStatus(LeaseTermStatus.NEW);
        LeaseTerm previousTerm = getPreviousTerm();
        BigInteger sequence = BigInteger.ONE;
        if (previousTerm != null) {
            sequence = previousTerm.getSequence().add(BigInteger.ONE);
            setFrequency(previousTerm.getFrequency());
        }
        setSequence(sequence);
    }

    protected void update() {
        // check endDate and startDate relationship
        final LeaseTerm nextTerm = getNextTerm();
        if (nextTerm != null && nextTerm.getStartDate() != null && (getEndDate() == null || nextTerm.getStartDate().compareTo(getEndDate().plusDays(1)) != 1)) {
            setEndDate(nextTerm.getStartDate().minusDays(1));
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public BigDecimal valueForDueDate(LocalDate dueDate) {
        return getApprovedValue();
    }

    @Programmatic
    BigDecimal valueForPeriod(InvoicingFrequency frequency, LocalDate periodStartDate, LocalDate dueDate) {
        if (getStatus() == LeaseTermStatus.APPROVED) {
            BigDecimal value = invoiceCalculationService.calculatedValue(this, periodStartDate, dueDate, frequency);
            return value;
        }
        return BigDecimal.ZERO;
    }

    // }}

    // //////////////////////////////////////

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("leaseItem", getLeaseItem()).add("sequence", getSequence()).toString();
    }

    // //////////////////////////////////////

    @Override
    @Hidden
    public int compareTo(LeaseTerm o) {
        return ORDERING_BY_LEASE_ITEM.compound(ORDERING_BY_SEQUENCE_ASC).compare(this, o);
    }

    // TODO: [JWA] After running the fixtures ordering by lease throws an error
    // but that seems impossible. Maybe out of sync with JDO?

    public static Ordering<LeaseTerm> ORDERING_BY_LEASE_ITEM = new Ordering<LeaseTerm>() {
        public int compare(LeaseTerm p, LeaseTerm q) {
            return Ordering.natural().nullsFirst().compare(p.getLeaseItem(), q.getLeaseItem());
        }
    };

    // REVIEW: the integration tests fail if this is made DESCending.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final static Ordering<LeaseTerm> ORDERING_BY_SEQUENCE_ASC = (Ordering) WithSequence.ORDERING_BY_SEQUENCE_ASC;

    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    private final static Ordering<LeaseTerm> ORDERING_BY_START_DATE_DESC = (Ordering) WithStartDate.ORDERING_BY_START_DATE_DESC;

    // //////////////////////////////////////

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

}
