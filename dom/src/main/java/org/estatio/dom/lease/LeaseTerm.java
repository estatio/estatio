package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.apache.commons.lang.NotImplementedException;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceCalculationService;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.utils.Orderings;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "LEASETERM_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class LeaseTerm extends EstatioTransactionalObject implements Comparable<LeaseTerm> {

    private LeaseItem leaseItem;

    @Hidden(where = Where.REFERENCES_PARENT)
    @MemberOrder(sequence = "1")
    @Persistent
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

    private LocalDate startDate;

    @Persistent
    @Title(sequence = "2", append = "-")
    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private LocalDate endDate;

    @Persistent
    @MemberOrder(sequence = "3")
    @Title(sequence = "3")
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    private BigDecimal value;

    @MemberOrder(sequence = "4")
    @Column(scale = 2)
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

    private LeaseTerm previousTerm;

    @Persistent(mappedBy = "nextTerm")
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
    private Set<InvoiceItem> invoiceItems = new LinkedHashSet<InvoiceItem>();

    @MemberOrder(sequence = "1")
    @Render(Type.EAGERLY)
    public Set<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(final Set<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public void addToInvoiceItems(final InvoiceItem invoiceItem) {
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

    public void removeFromInvoiceItems(final InvoiceItem invoiceItem) {
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
        for (InvoiceItem invoiceItem : getInvoiceItems()) {
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
    public InvoiceItem findOrCreateUnapprovedInvoiceItemFor(LocalDate startDate, LocalDate dueDate) {
        InvoiceItem ii = findUnapprovedInvoiceItemFor(startDate, dueDate);
        if (ii == null) {
            ii = invoiceRepository.newInvoiceItem();
            ii.modifyLeaseTerm(this);
        }
        return ii;
    }

    @Hidden
    public InvoiceItem findUnapprovedInvoiceItemFor(LocalDate startDate, LocalDate dueDate) {
        for (InvoiceItem invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW)) && startDate.equals(invoiceItem.getStartDate()) && dueDate.equals(invoiceItem.getDueDate())) {
                return invoiceItem;
            }
        }
        return null;
    }

    @Hidden
    public BigDecimal invoicedValueFor(LocalDate date) {
        BigDecimal invoicedValue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (InvoiceItem invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if (invoice == null || invoice.getStatus() == InvoiceStatus.NEW || invoiceItem.getStartDate() == null || !invoiceItem.getStartDate().equals(startDate)) {
                continue;
            }
            invoicedValue.add(invoiceItem.getNetAmount());
        }
        return invoicedValue;
    }

    @Hidden
    public BigDecimal valueForDueDate(LocalDate dueDate) {
        throw new NotImplementedException();
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
        if (getStartDate() != null && getStartDate().compareTo(LocalDate.now()) < 0) {
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
        LocalDate newStartDate = this.getEndDate() == null ? this.getFrequency().nextDate(this.getStartDate()) : this.getEndDate().plusDays(1);
        return createNext(newStartDate);
    }

    @Hidden
    public LeaseTerm createNext(LocalDate nextStartDate) {
        LocalDate endDate = getLeaseItem().getEndDate();
        LocalDate maxEndDate = endDate == null ? LocalDate.now().plusYears(1) : endDate;
        if (nextStartDate.isAfter(maxEndDate)) {
            // date is after end date, do nothing
            return null;
        } else {
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
    }

    @Hidden
    public void initialize() {
        setStatus(LeaseTermStatus.NEW);
        if (getPreviousTerm() == null) {
            setSequence(BigInteger.ONE);
        } else {
            setSequence(getPreviousTerm().getSequence().add(BigInteger.ONE));
        }
    }

    @Hidden
    public void update() {
        // check endDate and startDate relationship
        LeaseTerm nextTerm = getNextTerm();
        if (nextTerm != null && nextTerm.getStartDate() != null && (getEndDate() == null || nextTerm.getStartDate().compareTo(getEndDate().plusDays(1)) != 1)) {
            setEndDate(nextTerm.getStartDate().minusDays(1));
        }
    }

    @MemberOrder(name = "invoiceItems", sequence = "2")
    public LeaseTerm calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due Date") LocalDate dueDate) {
        if (getStatus() == LeaseTermStatus.APPROVED) {
            invoiceCalculationService.calculateAndInvoiceItems(this, startDate, dueDate);
            informUser("Calculated"+ this.getLeaseItem().getLease().getReference());
            //TODO: use the title of this term? But how access it.
        }
        return this;
    }

    // }}

    // {{ CompareTo
    @Override
    @Hidden
    @NotContributed
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
            return Orderings.lOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };

    // }}

    // {{ Injected services
    private Invoices invoiceRepository;

    public void setInvoiceService(Invoices service) {
        this.invoiceRepository = service;
    }

    private InvoiceCalculationService invoiceCalculationService;

    public void setInvoiceCalculationService(InvoiceCalculationService invoiceCalculationService) {
        this.invoiceCalculationService = invoiceCalculationService;
    }

    // }}

}
