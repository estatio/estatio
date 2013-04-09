package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.estatio.dom.invoice.InvoiceCalculator;
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

    // {{ Lease (property)
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

    // }}

    // {{ Sequence (property)
    private BigInteger sequence;

    @Hidden
    @Optional
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    // }}


    // {{ StartDate (property)
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

    // }}

    // {{ EndDate (property)
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

    // }}

    // {{ Value (property)
    private BigDecimal value;

    @MemberOrder(sequence = "4")
    @Column(scale = 4)
    @Mask("")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    // }}

    // {{ PreviousTerm (property)
    private LeaseTerm previousTerm;

    @Persistent(mappedBy="nextTerm")
    @MemberOrder(sequence = "1")
    @Hidden
    @Optional
    public LeaseTerm getPreviousTerm() {
        return previousTerm;
    }

    public void setPreviousTerm(final LeaseTerm previousTerm) {
        this.previousTerm = previousTerm;
    }

    public void modifyPreviousTerm(LeaseTerm term) {
        this.setPreviousTerm(term);
        term.setNextTerm(this); // not strictly necessary, as JDO will also do this (bidir link)
    }

    // }}

    // {{ NextTerm (property)
    private LeaseTerm nextTerm;

    @MemberOrder(sequence = "1")
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
        term.setPreviousTerm(this); // not strictly necessary, as JDO will also do this (bidir link)
    }

    public void clearNextTerm() {
        LeaseTerm nextTerm = getNextTerm();
        if(nextTerm != null) {
            nextTerm.setPreviousTerm(null);
            setNextTerm(null);
        }
    }

    // }}

    // {{ Status (property)
    private LeaseTermStatus status;
    @Disabled // maintained through LeaseTermContributedActions
    @MemberOrder(sequence = "1")
    public LeaseTermStatus getStatus() {
        return status;
    }

    public void setStatus(final LeaseTermStatus status) {
        this.status = status;
    }

    // }}

    // {{ InvoiceItems (Collection)
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

    // }}

    @Hidden
    public void removeUnapprovedInvoiceItemsForDate(LocalDate startDate) {
        for (InvoiceItem invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW)) && startDate.equals(invoiceItem.getStartDate())) {
                // remove item from term collection
                removeFromInvoiceItems(invoiceItem);
                // remove item from invoice
                if (invoice != null) {
                    invoice.removeFromItems(invoiceItem);
                }
                // remove from database
                resolve(invoiceItem);
                remove(invoiceItem);
            }
        }
    }

    @Hidden
    public BigDecimal invoicedValueFor(LocalDate startDate) {
        BigDecimal invoicedValue = BigDecimal.ZERO;
        for (InvoiceItem item : getInvoiceItems()) {
            if (item.getStartDate() == null || item.getStartDate().equals(startDate)) {
                // retrieve current value
                invoicedValue.add(item.getNetAmount());
            }
        }
        return invoicedValue;
    }

    @Hidden
    public InvoiceItem findOrCreateInvoiceItemFor(LocalDate startDate) {
        InvoiceItem ii = unapprovedInvoiceItemFor(startDate);
        if (ii == null) {
            ii = invoiceRepository.newInvoiceItem();
            invoiceItems.add(ii);
            ii.setLeaseTerm(this);
        }
        return ii;
    }

    @Hidden
    public InvoiceItem unapprovedInvoiceItemFor(LocalDate startDate) {
        for (InvoiceItem invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW)) && startDate.equals(invoiceItem.getStartDate())) {
                return invoiceItem;
            }
        }
        return null;
    }

    // {{ Actions
    @MemberOrder(sequence="1")
    public LeaseTerm verify() {
        new NotImplementedException();
        return this;
    }

    @MemberOrder(name="invoiceItems", sequence="2")
    public LeaseTerm calculate(@Named("Due date") LocalDate date) {
        // removeUnapprovedInvoiceItemsForDate(date);
        InvoiceCalculator ic = new InvoiceCalculator(this, date);
        ic.calculateAndInvoiceItems();
        return this;
    }

    @MemberOrder(sequence="3")
    public LeaseTerm approve() {
        setStatus(LeaseTermStatus.APPROVED);
        return this;
    }

    public String disableApprove() {
        return this.getStatus() == LeaseTermStatus.NEW ? null : "Cannot approve. Already approved?";
    }

    @MemberOrder(sequence="4")
    public LeaseTerm createOrUpdateNext() {
        new NotImplementedException();
        return null;
    }
    
    @Hidden
    public LeaseTerm createOrUpdateNext(LocalDate newStartDate) {
        LocalDate endDate = getLeaseItem().getEndDate();
        LocalDate maxEndDate = endDate == null ? LocalDate.now().plusYears(1) : endDate;
        if (newStartDate.isAfter(maxEndDate)) {
            // date is after end date, do nothing
            return null;
        } else {
            LeaseTerm term = getNextTerm();
            if (getNextTerm() == null) {
                term = getLeaseItem().createNextTerm(this);
            }
            // new start Date
            term.setStartDate(newStartDate);
            this.setEndDate(newStartDate.minusDays(1));
            return term;
        }
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

    // }}

}
