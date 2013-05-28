package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Paged;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.utils.CalendarUtils;
import org.estatio.dom.utils.Orderings;
import org.estatio.services.clock.ClockService;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class LeaseItem extends EstatioTransactionalObject implements Comparable<LeaseItem> {

    
    private Lease lease;

    @Hidden(where = Where.PARENTED_TABLES)
    @Title(sequence = "1", append = ":")
    @MemberOrder(sequence = "1")
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    public void modifyLease(final Lease lease) {
        Lease currentLease = getLease();
        if (lease == null || lease.equals(currentLease)) {
            return;
        }
        lease.addToItems(this);
    }

    public void clearLease() {
        Lease currentLease = getLease();
        if (currentLease == null) {
            return;
        }
        currentLease.removeFromItems(this);
    }

    
    private BigInteger sequence;

    @MemberOrder(sequence = "1")
    @Hidden
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    private LeaseItemType type;

    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public LeaseItemType getType() {
        return type;
    }

    public void setType(final LeaseItemType type) {
        this.type = type;
    }

    
    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @MemberOrder(sequence = "3")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    
    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(sequence = "4")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate calculatedEndDate() {
        return getEndDate() == null ? getLease().getEndDate() : getEndDate();
    }

    
//    @javax.jdo.annotations.Persistent
//    private LocalDate tenancyStartDate;
//
//    @Optional
//    @MemberOrder(sequence = "5")
//    @Hidden(where = Where.PARENTED_TABLES)
//    public LocalDate getTenancyStartDate() {
//        return tenancyStartDate;
//    }
//
//    public void setTenancyStartDate(final LocalDate tenancyStartDate) {
//        this.tenancyStartDate = tenancyStartDate;
//    }
//
//    
//    @javax.jdo.annotations.Persistent
//    private LocalDate tenancyEndDate;
//
//    @Optional
//    @MemberOrder(sequence = "6")
//    @Hidden(where = Where.PARENTED_TABLES)
//    public LocalDate getTenancyEndDate() {
//        return tenancyEndDate;
//    }
//
//    public void setTenancyEndDate(final LocalDate tenancyEndDate) {
//        this.tenancyEndDate = tenancyEndDate;
//    }

    private InvoicingFrequency invoicingFrequency;

    @MemberOrder(sequence = "12")
    @Hidden(where = Where.PARENTED_TABLES)
    public InvoicingFrequency getInvoicingFrequency() {
        return invoicingFrequency;
    }

    public void setInvoicingFrequency(final InvoicingFrequency invoicingFrequency) {
        this.invoicingFrequency = invoicingFrequency;
    }

    private PaymentMethod paymentMethod;

    @MemberOrder(sequence = "13")
    @Hidden(where = Where.PARENTED_TABLES)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private Charge charge;

    @MemberOrder(sequence = "14")
    public Charge getCharge() {
        return charge;
    }

    public void setCharge(final Charge charge) {
        this.charge = charge;
    }

    public List<Charge> choicesCharge() {
        return charges.allCharges();
    }

    @Disabled
    @Optional
    // TODO: Wicket still marks disabled fields a mandatory... it shouldn't.
    public BigDecimal getCurrentValue() {
        return valueForDate(clockService.now());
    }

    @Hidden
    public BigDecimal valueForDate(LocalDate date) {
        for (LeaseTerm term : getTerms()) {
            if (CalendarUtils.isBetween(date, term.getStartDate(), term.getEndDate())) {
                return term.getValue();
            }
        }
        return null;
    }

    
    
    @javax.jdo.annotations.Persistent(mappedBy = "leaseItem")
    private SortedSet<LeaseTerm> terms = new TreeSet<LeaseTerm>();

    @Render(Type.EAGERLY)
    @MemberOrder(name = "Terms", sequence = "15")
    @Paged(15)
    public SortedSet<LeaseTerm> getTerms() {
        return terms;
    }

    public void setTerms(final SortedSet<LeaseTerm> terms) {
        this.terms = terms;
    }

    public void addToTerms(final LeaseTerm term) {
        if (term == null || getTerms().contains(term)) {
            return;
        }
        term.clearLeaseItem();
        term.setLeaseItem(this);
        getTerms().add(term);
    }

    public void removeFromTerms(final LeaseTerm term) {
        if (term == null || !getTerms().contains(term)) {
            return;
        }
        term.setLeaseItem(null);
        getTerms().remove(term);
    }

    @Hidden
    public LeaseTerm findTerm(LocalDate startDate) {
        for (LeaseTerm term : getTerms()) {
            if (startDate.equals(term.getStartDate())) {
                return term;
            }
        }
        return null;
    }

    @Hidden
    public LeaseTerm findTermWithSequence(BigInteger sequence) {
        for (LeaseTerm term : getTerms()) {
            if (sequence.equals(term.getSequence())) {
                return term;
            }
        }
        return null;
    }

    
    @MemberOrder(name = "terms", sequence = "11")
    public LeaseTerm createInitialTerm() {
        LeaseTerm term = leaseTerms.newLeaseTerm(this);
        return term;
    }

    public String disableCreateInitialTerm() {
        return getTerms().size() > 0 ? "Use either 'Verify' or 'Create Next Term' on last term" : null;
    }

    
    
    @Hidden
    @MemberOrder(name = "terms", sequence = "11")
    public LeaseTerm createNextTerm(LeaseTerm currentTerm) {
        LeaseTerm term = leaseTerms.newLeaseTerm(this, currentTerm);
        return term;
    }

    public LeaseItem verify() {
        for (LeaseTerm term : getTerms()) {
            if (term.getPreviousTerm() == null) {
                // since verify is recursive on terms only start on the main
                // term
                term.verify();
            }
        }
        return this;
    }

    public LeaseItem calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due date") LocalDate dueDate) {
        for (LeaseTerm term : getTerms()) {
            term.calculate(startDate, dueDate);
        }
        return this;
    }

    @Hidden
    BigDecimal valueForPeriod(InvoicingFrequency frequency, LocalDate periodStartDate, LocalDate dueDate) {
        BigDecimal total = new BigDecimal(0);
        for (LeaseTerm term : getTerms()) {
            total = total.add(term.valueForPeriod(frequency, periodStartDate, dueDate));
        }
        return total;
    }

    
    // {{ Comparable impl
    @Override
    public int compareTo(LeaseItem o) {
        return ORDERING_BY_TYPE.compound(ORDERING_BY_START_DATE).compare(this, o);
    }

    public static Ordering<LeaseItem> ORDERING_BY_TYPE = new Ordering<LeaseItem>() {
        public int compare(LeaseItem p, LeaseItem q) {
            return LeaseItemType.ORDERING_NATURAL.compare(p.getType(), q.getType());
        }
    };

    public final static Ordering<LeaseItem> ORDERING_BY_START_DATE = new Ordering<LeaseItem>() {
        public int compare(LeaseItem p, LeaseItem q) {
            return Orderings.LOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };
    // }}

    
    // {{ injected
    private Charges charges;
    
    public void injectCharges(Charges charges) {
        this.charges = charges;
    }
    
    private LeaseTerms leaseTerms;
    
    public void injectLeaseTerms(LeaseTerms leaseTerms) {
        this.leaseTerms = leaseTerms;
    }
    
    private ClockService clockService;
    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }
    // }}

}
