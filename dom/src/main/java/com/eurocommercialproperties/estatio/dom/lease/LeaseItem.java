package com.eurocommercialproperties.estatio.dom.lease;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Resolve;
import org.apache.isis.applib.annotation.Resolve.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.index.Index;

@PersistenceCapable
public class LeaseItem extends AbstractDomainObject {

    // {{ Lease (property)
    private Lease lease;

    @Hidden(where = Where.PARENTED_TABLES)
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

    @Title
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

    // {{ Index (property)
    private Index index;

    @MemberOrder(sequence = "10")
    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    // }}

    // {{ IndexationFrequency (property)
    private IndexationFrequency indexationFrequency;

    @MemberOrder(sequence = "11")
    public IndexationFrequency getIndexationFrequency() {
        return indexationFrequency;
    }

    public void setIndexationFrequency(final IndexationFrequency indexationFrequency) {
        this.indexationFrequency = indexationFrequency;
    }

    // }}

    // {{ InvoicingFrequency (property)
    private InvoiceFrequency invoicingFrequency;

    @MemberOrder(sequence = "12")
    public InvoiceFrequency getInvoicingFrequency() {
        return invoicingFrequency;
    }

    public void setInvoicingFrequency(final InvoiceFrequency invoicingFrequency) {
        this.invoicingFrequency = invoicingFrequency;
    }

    // }}

    // {{ Terms (Collection)
    private Set<LeaseTerm> terms = new LinkedHashSet<LeaseTerm>();

    @Resolve(Type.EAGERLY)
    @Persistent(mappedBy = "leaseItem")
    @MemberOrder(sequence = "2")    
    public Set<LeaseTerm> getTerms() {
        return terms;
        //TODO: Q: what's the best way to sort these terms? 
    }

    public void setTerms(final Set<LeaseTerm> terms) {
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
