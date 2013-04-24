package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.utils.CalenderUtils;
import org.estatio.dom.utils.Orderings;

@PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class LeaseItem extends AbstractDomainObject implements Comparable<LeaseItem> {

    // {{ Lease (property)
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

    // }}

    // {{ Sequence (property)
    private BigInteger sequence;

    @MemberOrder(sequence = "1")
    @Hidden
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    // }}

    // {{ LeaseItemType (property)
    private LeaseItemType type;

    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public LeaseItemType getType() {
        return type;
    }

    public void setType(final LeaseItemType type) {
        this.type = type;
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @Persistent
    @MemberOrder(sequence = "3")
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
    @MemberOrder(sequence = "4")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // }}

    // {{ TenancyStartDate (property)
    private LocalDate tenancyStartDate;

    @Persistent
    @Optional
    @MemberOrder(sequence = "5")
    @Hidden(where = Where.PARENTED_TABLES)
    public LocalDate getTenancyStartDate() {
        return tenancyStartDate;
    }

    public void setTenancyStartDate(final LocalDate tenancyStartDate) {
        this.tenancyStartDate = tenancyStartDate;
    }

    // }}

    // {{ TenancyEndDate (property)
    private LocalDate tenancyEndDate;

    @Persistent
    @Optional
    @MemberOrder(sequence = "6")
    @Hidden(where = Where.PARENTED_TABLES)
    public LocalDate getTenancyEndDate() {
        return tenancyEndDate;
    }

    public void setTenancyEndDate(final LocalDate tenancyEndDate) {
        this.tenancyEndDate = tenancyEndDate;
    }

    // }}

    // {{ NextDueDate (property)
    private LocalDate nextDueDate;

    @MemberOrder(sequence = "7")
    @Disabled
    @Optional
    // TODO: Wicket still marks disabled fields a mandatory. Don't know if that
    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(final LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    // }}

    // {{ InvoicingFrequency (property)
    private InvoicingFrequency invoicingFrequency;

    @MemberOrder(sequence = "12")
    @Hidden(where = Where.PARENTED_TABLES)
    public InvoicingFrequency getInvoicingFrequency() {
        return invoicingFrequency;
    }

    public void setInvoicingFrequency(final InvoicingFrequency invoicingFrequency) {
        this.invoicingFrequency = invoicingFrequency;
    }

    // }}

    // {{ PayymentMethod (property)
    private PaymentMethod paymentMethod;

    @MemberOrder(sequence = "13")
    @Hidden(where = Where.PARENTED_TABLES)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // }}

    // {{ Charge (property)
    private Charge charge;

    @MemberOrder(sequence = "14")
    public Charge getCharge() {
        return charge;
    }

    public void setCharge(final Charge charge) {
        this.charge = charge;
    }

    public List<Charge> choicesCharge() {
        return chargeService.allCharges();
    }

    // }}

    // {{ CurrentValue
    @Disabled
    @Optional
    // TODO: Wicket still marks disabled fields a mandatory. Don't know if that
    public BigDecimal getCurrentValue() {
        return getValueForDate(LocalDate.now());
    }

    @Hidden
    public BigDecimal getValueForDate(LocalDate date) {
        for (LeaseTerm term : getTermsWorkaround()) {
            if (CalenderUtils.isBetween(date, term.getStartDate(), term.getEndDate())) {
                return term.getValue();
            }
        }
        return null;
    }

    // }}

    // {{ Terms (Collection)
    private SortedSet<LeaseTerm> terms = new TreeSet<LeaseTerm>();

    @Render(Type.EAGERLY)
    @Persistent(mappedBy = "leaseItem")
    @MemberOrder(name = "Terms", sequence = "15")
    public SortedSet<LeaseTerm> getTerms() {
        return terms;
    }

    @Hidden
    public SortedSet<LeaseTerm> getTermsWorkaround() {
        // TOFIX: a workaround until we figure out how to get
        // JDO/DN to callback on the lazy loading of this collection
        // return terms;
        // if (this.terms == null) {
        // // this can happen, it would seem, by JDO/DN when it is setting up
        // the object
        // // with its own set impl for lazy loading. It would seem that it
        // could be null...
        // return null;
        // } else {
        // // inject each element before returning it
        // return Sets.newTreeSet(Iterables.transform(this.terms, new
        // Function<LeaseTerm, LeaseTerm>(){
        // public LeaseTerm apply(LeaseTerm leaseTerm) {
        // leaseTerm.getStartDate(); // force lazy loading callback.
        // return leaseTerm;
        // }
        // }));
        // }

        if (getTerms() == null) {
            // this can happen, it would seem, by JDO/DN when it is setting up
            // the object
            // with its own set impl for lazy loading. It would seem that it
            // could be null...
            return null;
        } else {
            if (isisJdoSupport == null) {
                return getTerms(); // otherwise I have to inject this in every single unit test.
            } else {
                // inject each element before returning it
                return Sets.newTreeSet(Iterables.transform(getTerms(), new Function<LeaseTerm, LeaseTerm>() {
                    public LeaseTerm apply(LeaseTerm leaseTerm) {
                        return isisJdoSupport.injected(leaseTerm);
                    }
                }));
            }
        }

    }

    public void setTerms(final SortedSet<LeaseTerm> terms) {
        this.terms = terms;
    }

    public void addToTerms(final LeaseTerm term) {
        // check for no-op
        if (term == null || getTermsWorkaround().contains(term)) {
            return;
        }
        // dissociate arg from its current parent (if any).
        term.clearLeaseItem();
        // associate arg
        term.setLeaseItem(this);
        getTerms().add(term);
        // additional business logic
        // onAddToTerms(term);
    }

    public void removeFromTerms(final LeaseTerm term) {
        // check for no-op
        if (term == null || !getTermsWorkaround().contains(term)) {
            return;
        }
        // dissociate arg
        term.setLeaseItem(null);
        getTerms().remove(term);
        // additional business logic
        // onRemoveFromTerms(term);
    }

    @Hidden
    public LeaseTerm findTerm(LocalDate startDate) {
        for (LeaseTerm term : getTermsWorkaround()) {
            if (startDate.equals(term.getStartDate())) {
                return term;
            }
        }
        return null;
    }

    @Hidden
    public LeaseTerm findTermWithSequence(BigInteger sequence) {
        for (LeaseTerm term : getTermsWorkaround()) {
            if (sequence.equals(term.getSequence())) {
                return term;
            }
        }
        return null;
    }

    @MemberOrder(name = "terms", sequence = "11")
    public LeaseTerm createInitialTerm() {
        LeaseTerm term = leaseTermsService.newLeaseTerm(this);
        return term;
    }

    public String disableCreateInitialTerm() {
        return getTermsWorkaround().size() > 0 ? "Use either 'Verify' or 'Create Next Term' on last term" : null;
    }

    @Hidden
    @MemberOrder(name = "terms", sequence = "11")
    public LeaseTerm createNextTerm(LeaseTerm currentTerm) {
        LeaseTerm term = leaseTermsService.newLeaseTerm(this, currentTerm);
        return term;
    }

    // }}

    // {{ Actions

    public LeaseItem verify() {
        for (LeaseTerm term : getTermsWorkaround()) {
            if (term.getPreviousTerm() == null) {
                // since verify is recursive on terms only start on the main
                // term
                term.verify();
            }
        }
        return this;
    }

    public LeaseItem calculate(@Named("Due date") LocalDate dueDate) {
        // SortedSet<LeaseTerm> terms =
        // Sets.newTreeSet(Iterables.transform(getTerms(), new
        // Function<LeaseTerm, LeaseTerm>(){
        // public LeaseTerm apply(LeaseTerm leaseTerm) {
        // return isisServiceInjector.injected(leaseTerm);
        // }
        // }));

        for (LeaseTerm term : getTermsWorkaround()) {
            // resolve(term); // TODO: need to call resolve,
            // // otherwise services are not injected
            // // when running in the wicket viewer.
            term.calculate(dueDate);
        }
        return this;
    }

    // }}

    // {{ Injected Services

    private Charges chargeService;

    public void setChargeService(Charges charges) {
        this.chargeService = charges;
    }

    private LeaseTerms leaseTermsService;

    public void setLeaseTermsService(LeaseTerms leaseTerms) {
        this.leaseTermsService = leaseTerms;
    }

    // }}

    // {{ Comparable

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
            return Orderings.lOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };

    // }}

    // {{ services
    private IsisJdoSupport isisJdoSupport;

    public void setIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }
    // }}
}
