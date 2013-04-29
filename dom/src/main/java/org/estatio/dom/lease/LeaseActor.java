package org.estatio.dom.lease;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.DateRange;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

@PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class LeaseActor extends EstatioTransactionalObject implements Comparable<LeaseActor> {

    // {{ Lease (property)
    private Lease lease;

    @Title(sequence = "3", prepend = ":")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }
    
    public void modifyLease(final Lease lease) {
        Lease currentLease = getLease();
        // check for no-op
        if (lease == null || lease.equals(currentLease)) {
            return;
        }
        // associate new
        setLease(lease);
        // additional business logic
        // onModifyLease(currentLease, lease);
    }

    public void clearLease() {
        Lease currentLease = getLease();
        // check for no-op
        if (currentLease == null) {
            return;
        }
        // dissociate existing
        setLease(null);
        // additional business logic
        // onClearLease(currentLease);
    }

    // }}

    // {{ Party (property)
    private Party party;

    @Title(sequence = "2", prepend = ":")
    @MemberOrder(sequence = "2")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // }}

    // {{ Type (property)
    private LeaseActorType type;

    @Title(sequence = "1")
    @MemberOrder(sequence = "3")
    public LeaseActorType getType() {
        return type;
    }

    public void setType(final LeaseActorType type) {
        this.type = type;
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @MemberOrder(sequence = "4")
    @Optional
    @Persistent
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @MemberOrder(sequence = "5")
    @Optional
    @Persistent
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // }}

    /**
     * This is necessary but not sufficient; in
     * {@link Lease#addRole(Party, LeaseActorType, LocalDate, LocalDate)} there
     * is logic to ensure that there cannot be two {@link LeaseActor actor}s of
     * the same type at the same point in time.
     * 
     * TODO: need to implement the above statement!!!
     */
    @Override
    @Hidden
    public int compareTo(LeaseActor o) {
        int compareType = this.getType().compareTo(o.getType());
        if (compareType != 0) {
            return compareType;
        }
        if (this.getStartDate() == null && o.getStartDate() != null) {
            return -1;
        }
        if (this.getStartDate() != null && o.getStartDate() == null) {
            return +1;
        }
        if (this.getStartDate() == null && o.getStartDate() == null) {
            return 0;
        }
        return this.getStartDate().compareTo(o.getStartDate());
    }

    // {[
    public boolean isCurrent() {
        return isActiveOn(LocalDate.now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return new DateRange(getStartDate(), getEndDate()).contains(localDate);
    }
    // }}

}
