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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithSequence;
import org.estatio.dom.WithStatus;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannel;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.utils.ValueUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "LEASETERM_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "LEASE_TERM_IDX",
                members = { "leaseItem", "sequence" }),
        @javax.jdo.annotations.Index(
                name = "LEASE_TERM2_IDX", 
                members = { "leaseItem", "startDate" }) })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByStatusAndActiveDate", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE status == :status "
                        + "&& startDate <= :date "
                        + "&& (endDate == null || endDate >= :date)"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndSequence", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem "
                        + "&& sequence == :sequence"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndStartDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.lease.LeaseTerm " +
                        "WHERE leaseItem == :leaseItem " +
                        "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndEndDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.lease.LeaseTerm " +
                        "WHERE leaseItem == :leaseItem " +
                        "&& endDate == :endDate"),
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@MemberGroups({ "General", "Dates", "Related" })
public abstract class LeaseTerm extends EstatioTransactionalObject<LeaseTerm, LeaseTermStatus> implements WithIntervalMutable<LeaseTerm>, WithSequence {

    public LeaseTerm() {
        // TODO: the integration tests fail if this is made DESCending.
        super("leaseItem, sequence", LeaseTermStatus.APPROVED, LeaseTermStatus.NEW);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "LEASEITEM_ID")
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
    @MemberOrder(name = "Dates", sequence = "2")
    @Optional
    @Disabled
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public void modifyStartDate(final LocalDate startDate) {
        LocalDate currentStartDate = getStartDate();
        if (startDate == null || startDate.equals(currentStartDate)) {
            return;
        }
        setStartDate(startDate);
        if (getPrevious() != null) {
            getPrevious().modifyEndDate(getInterval().endDateFromStartDate());
        }
    }

    public void clearStartDate() {
        LocalDate currentStartDate = getStartDate();
        if (currentStartDate == null) {
            return;
        }
        setStartDate(null);
        // TODO: shouldn't there be some logic reciprocal to that in modifyStartDate ?
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(name = "Dates", sequence = "3")
    @Title(sequence = "3")
    @Disabled
    @Optional
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    public void modifyEndDate(final LocalDate endDate) {
        LocalDate currentEndDate = getEndDate();
        if (endDate == null || endDate.equals(currentEndDate)) {
            return;
        }
        setEndDate(endDate);
    }
    
    public void clearEndDate() {
        LocalDate currentEndDate = getEndDate();
        if (currentEndDate == null) {
            return;
        }
        setEndDate(null);
    }

    // //////////////////////////////////////

    @MemberOrder(name="endDate", sequence="1")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public LeaseTerm changeDates(
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        modifyStartDate(startDate);
        modifyEndDate(endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate) {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }
    
    @Override
    public LocalDate default0ChangeDates() {
        return getStartDate();
    }
    @Override
    public LocalDate default1ChangeDates() {
        return getEndDate();
    }
    
    @Override
    public String validateChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate) {
        return startDate.isBefore(endDate)?null:"Start date must be before end date";
    }
    // //////////////////////////////////////

    @Programmatic
    @Override
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        Lease lease = getLeaseItem().getLease();
        return LocalDateInterval.including(getStartDate(), getEndDate()).overlap(lease.getEffectiveInterval());
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
    
    @Override
    public void created() {
        super.created();
        setStatus(LeaseTermStatus.NEW);
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

    @javax.jdo.annotations.Column(name = "PREVIOUS_ID")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    private LeaseTerm previous;

    @MemberOrder(name = "Related", sequence = "6")
    @Named("Previous Term")
    @Hidden(where = Where.ALL_TABLES)
    @Optional
    public LeaseTerm getPrevious() {
        return previous;
    }

    public void setPrevious(final LeaseTerm previous) {
        this.previous = previous;
    }

    public void modifyPrevious(final LeaseTerm previous) {
        LeaseTerm currentPrevious = getPrevious();
        if (previous == null || previous.equals(currentPrevious)) {
            return;
        }
        clearPrevious();
        previous.setNext(this);
        setPrevious(previous);
    }

    public void clearPrevious() {
        LeaseTerm currentPrevious = getPrevious();
        if (currentPrevious == null) {
            return;
        }
        currentPrevious.setNext(null);
        setPrevious(null);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "NEXT_ID")
    private LeaseTerm next;

    @Hidden(where = Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "7")
    @Named("Next Term")
    @Optional
    public LeaseTerm getNext() {
        return next;
    }

    public void setNext(final LeaseTerm next) {
        this.next = next;
    }

    public void modifyNext(final LeaseTerm next) {
        LeaseTerm currentNext = getNext();
        if (next == null || next.equals(currentNext)) {
            return;
        }
        if (currentNext != null) {
            currentNext.clearPrevious();
        }
        next.modifyPrevious(this);
    }

    public void clearNext() {
        LeaseTerm currentNext = getNext();
        if (currentNext == null) {
            return;
        }
        currentNext.clearPrevious();
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
    @Hidden
    public void remove() {
        if (getNext() != null) {
            getNext().remove();
        }
        if (this.getInvoiceItems().size() > 0) {
            // TODO: this term is outside the scope of the lease termination
            // date and there are invoice items related to it so the amount
            // should be credited
        } else {
            this.modifyPrevious(null);
            this.modifyLeaseItem(null);
        }
    }

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
    public InvoiceItemForLease findOrCreateUnapprovedInvoiceItemFor(LeaseTerm leaseTerm, LocalDate startDate, LocalDate dueDate) {
        InvoiceItemForLease ii = findUnapprovedInvoiceItemFor(leaseTerm, startDate, dueDate);
        if (ii == null) {
            ii = invoiceItemsForLease.newInvoiceItem();
            ii.modifyLeaseTerm(this);
        }
        return ii;
    }

    @Programmatic
    public InvoiceItemForLease findUnapprovedInvoiceItemFor(LeaseTerm leaseTerm, LocalDate startDate, LocalDate dueDate) {
        for (InvoiceItemForLease invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW)) && leaseTerm.equals(invoiceItem.getLeaseTerm()) && startDate.equals(invoiceItem.getStartDate()) && dueDate.equals(invoiceItem.getDueDate())) {
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

    @Hidden
    public LeaseTerm calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due Date") LocalDate dueDate) {
        return calculate(startDate, dueDate, InvoiceRunType.NORMAL_RUN);
    }

    @MemberOrder(name = "invoiceItems", sequence = "2")
    public LeaseTerm calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due Date") LocalDate dueDate, @Named("Run Type") InvoiceRunType runType) {
        invoiceCalculationService.calculateAndInvoice(this, startDate, dueDate, getLeaseItem().getInvoicingFrequency(), runType);
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
            next = getNext();
            if (next != null) {
                next.verify();
            }
        }
        return this;
    }


    // //////////////////////////////////////

    @MemberOrder(sequence = "3")
    public LeaseTerm createNext() {
        LocalDate newStartDate = getEndDate() == null ? this.getFrequency().nextDate(this.getStartDate()) : this.getEndDate().plusDays(1);
        return createNext(newStartDate);
    }

    LeaseTerm createNext(LocalDate nextStartDate) {
        if (getNext() != null) {
            return null;
        }
        LocalDate terminationDate = getLeaseItem().getLease().getTerminationDate();
        if (terminationDate != null && terminationDate.isBefore(nextStartDate))
            return null;

        final LocalDate endDate = getLeaseItem().calculatedEndDate();
        final LocalDate oneYearFromNow = clockService.now().plusYears(1);
        final LocalDate maxEndDate = ValueUtils.coalesce(endDate, oneYearFromNow);
        if (nextStartDate.isAfter(maxEndDate)) {
            // date is after end date, do nothing
            return null;
        }

        LeaseTerm term = getNext();
        if (getNext() == null) {
            term = getLeaseItem().createNextTerm(this);
        }

        // new start Date
        term.modifyStartDate(nextStartDate);
        term.update();
        return term;
    }

    // //////////////////////////////////////

    protected void initialize() {
        setStatus(LeaseTermStatus.NEW);
        LeaseTerm previousTerm = getPrevious();
        BigInteger sequence = BigInteger.ONE;
        if (previousTerm != null) {
            sequence = previousTerm.getSequence().add(BigInteger.ONE);
            setFrequency(previousTerm.getFrequency());
        }
        setSequence(sequence);
    }

    protected void update() {
        // terminate the last term
        LocalDate terminationDate = getLeaseItem().getLease().getTerminationDate();
        if (terminationDate != null && next == null)
            if (getEndDate() == null || getEndDate().compareTo(terminationDate) > 0)
                setEndDate(terminationDate);
    }

    // //////////////////////////////////////

    @Programmatic
    public BigDecimal valueForDueDate(LocalDate dueDate) {
        return getTrialValue();
    }

    @Programmatic
    BigDecimal valueForPeriod(InvoicingFrequency frequency, LocalDate periodStartDate, LocalDate dueDate) {
        if (getStatus().isUnlocked()) {
            BigDecimal value = invoiceCalculationService.calculatedValue(this, periodStartDate, dueDate, frequency);
            return value;
        }
        return BigDecimal.ZERO;
    }

    // //////////////////////////////////////

    private InvoiceItemsForLease invoiceItemsForLease;

    public void injectInvoiceItemsForLease(InvoiceItemsForLease invoiceItemsForLease) {
        this.invoiceItemsForLease = invoiceItemsForLease;
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
