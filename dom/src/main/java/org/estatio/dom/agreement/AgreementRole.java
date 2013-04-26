package org.estatio.dom.agreement;

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
public class AgreementRole extends EstatioTransactionalObject implements Comparable<AgreementRole> {

    // {{ Agreement (property)
    private Agreement agreement;

    @Title(sequence = "3", prepend = ":")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(final Agreement lease) {
        this.agreement = lease;
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
    private AgreementRoleType type;

    @Title(sequence = "1")
    @MemberOrder(sequence = "3")
    public AgreementRoleType getType() {
        return type;
    }

    public void setType(final AgreementRoleType type) {
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
     * {@link Agreement#addActor(Party, AgreementRoleType, LocalDate, LocalDate)}
     * there is logic to ensure that there cannot be two {@link AgreementRole
     * actor}s of the same type at the same point in time.
     * 
     * TODO: need to implement the above statement!!!
     */
    @Override
    @Hidden
    public int compareTo(AgreementRole o) {
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
