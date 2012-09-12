package com.eurocommercialproperties.estatio.dom.lease;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Where;
import org.joda.time.LocalDate;

@PersistenceCapable
public class LeaseItem extends AbstractDomainObject {

    // {{ Lease (property)
    private Lease lease;

    @Hidden(where=Where.PARENTED_TABLES)
    @MemberOrder(sequence = "1")
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // }}

    // {{ LeaseItemType (property)
    private LeaseItemType type;

    @MemberOrder(sequence = "2")
    public LeaseItemType getLeaseItemType() {
        return type;
    }

    public void setLeaseItemType(final LeaseItemType type) {
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

    // {{ Terms (Collection)
    private List<LeaseTerm> terms = new ArrayList<LeaseTerm>();

    @Persistent(mappedBy = "leaseItem")
    @MemberOrder(sequence = "5")
    public List<LeaseTerm> getTerms() {
        return terms;
    }

    public void setTerms(final List<LeaseTerm> terms) {
        this.terms = terms;
    }

    public void addToTerms(final LeaseTerm leaseTerm) {
        // check for no-op
        if (leaseTerm == null || getTerms().contains(leaseTerm)) {
            return;
        }
        // associate new
        getTerms().add(leaseTerm);
        // additional business logic
        onAddToTerms(leaseTerm);
    }

    public void removeFromTerms(final LeaseTerm terms) {
        // check for no-op
        if (terms == null || !getTerms().contains(terms)) {
            return;
        }
        // dissociate existing
        getTerms().remove(terms);
        // additional business logic
        onRemoveFromTerms(terms);
    }

    protected void onAddToTerms(final LeaseTerm terms) {
    }

    protected void onRemoveFromTerms(final LeaseTerm terms) {
    }
    // }}

}
