package org.estatio.agreement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.utils.CalenderUtils;
import org.estatio.dom.utils.Orderings;
import org.joda.time.LocalDate;

import com.google.common.collect.Ordering;

@PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class AgreementItem extends AbstractDomainObject implements Comparable<AgreementItem> {

    // {{ Agreement (property)
    private Agreement lease;

    @Hidden(where = Where.PARENTED_TABLES)
    @Title(sequence="1", append = ":")
    @MemberOrder(sequence = "1")
    public Agreement getAgreement() {
        return lease;
    }

    public void setAgreement(final Agreement lease) {
        this.lease = lease;
    }

    // }}

    // {{ Type (property)
    private AgreementItemType type;

    @MemberOrder(sequence = "1")
    public AgreementItemType getType() {
        return type;
    }

    public void setType(final AgreementItemType type) {
        this.type = type;
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

    // {{ CurrentValue
    @Disabled
    @Optional //TODO: Wicket still marks disabled fields a mandatory. Don't know if that
    public BigDecimal getCurrentValue() {
        return getValueForDate(LocalDate.now());
    }

    @Hidden
    public BigDecimal getValueForDate(LocalDate date) {
        for (AgreementTerm term : getTerms()) {
            if (CalenderUtils.isBetween(date, term.getStartDate(), term.getEndDate())) {
                return term.getValue();
            }
        }
        return null;
    }

    // }}

    // {{ Terms (Collection)
    private SortedSet<AgreementTerm> terms = new TreeSet<AgreementTerm>();

    @Render(Type.EAGERLY)
    @Persistent(mappedBy = "leaseItem")
    @MemberOrder(name = "Terms", sequence = "15")
    public SortedSet<AgreementTerm> getTerms() {
        return terms;
    }

    public void setTerms(final SortedSet<AgreementTerm> terms) {
        this.terms = terms;
    }

    public void addToTerms(final AgreementTerm leaseTerm) {
        // check for no-op
        if (leaseTerm == null || getTerms().contains(leaseTerm)) {
            return;
        }
        // associate new
        getTerms().add(leaseTerm);
        leaseTerm.setAgreementItem(this);
    }

    public void removeFromTerms(final AgreementTerm leaseTerm) {
        // check for no-op
        if (leaseTerm == null || !getTerms().contains(leaseTerm)) {
            return;
        }
        // dissociate existing
        getTerms().remove(leaseTerm);
        leaseTerm.setAgreementItem(null);
    }

    @Hidden
    public AgreementTerm findTerm(LocalDate startDate) {
        for (AgreementTerm term : getTerms()) {
            if (startDate.equals(term.getStartDate())) {
                return term;
            }
        }
        return null;
    }

    @Hidden
    public AgreementTerm findTermWithSequence(BigInteger sequence) {
        for (AgreementTerm term : getTerms()) {
            if (sequence.equals(term.getSequence())) {
                return term;
            }
        }
        return null;
    }


    // }}

    // {{ Actions

    public AgreementItem verify() {
        for (AgreementTerm term : getTerms()) {
            term.verify();
        }
        return this;
    }

    // }}

    // {{ Comparable

    @Override
    public int compareTo(AgreementItem o) {
        return ORDERING_BY_TYPE.compound(ORDERING_BY_START_DATE).compare(this, o);
    }

    public static Ordering<AgreementItem> ORDERING_BY_TYPE = new Ordering<AgreementItem>() {
        public int compare(AgreementItem p, AgreementItem q) {
            return AgreementItemType.ORDERING_NATURAL.compare(p.getType(), q.getType());
        }
    };

    public final static Ordering<AgreementItem> ORDERING_BY_START_DATE = new Ordering<AgreementItem>() {
        public int compare(AgreementItem p, AgreementItem q) {
            return Orderings.lOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };

    // }}

}
