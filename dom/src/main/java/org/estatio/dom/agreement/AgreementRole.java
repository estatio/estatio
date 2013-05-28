package org.estatio.dom.agreement;

import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.LocalDateInterval;
import org.estatio.dom.utils.Orderings;
import org.estatio.dom.utils.LocalDateInterval.IntervalEnding;
import org.estatio.services.clock.ClockService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class AgreementRole extends EstatioTransactionalObject implements Comparable<AgreementRole> {

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

    public void modifyAgreement(final Agreement agreement) {
        Agreement currentAgreement = getAgreement();
        // check for no-op
        if (agreement == null || agreement.equals(currentAgreement)) {
            return;
        }
        // delegate to parent to associate
        agreement.addToRoles(this);
    }

    public void clearAgreement() {
        Agreement currentAgreement = getAgreement();
        // check for no-op
        if (currentAgreement == null) {
            return;
        }
        // delegate to parent to dissociate
        currentAgreement.removeFromRoles(this);
    }

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

    public void modifyParty(final Party party) {
        Party currentParty = getParty();
        // check for no-op
        if (party == null || party.equals(currentParty)) {
            return;
        }
        // delegate to parent to associate
        party.addToAgreements(this);
    }

    public void clearParty() {
        Party currentParty = getParty();
        // check for no-op
        if (currentParty == null) {
            return;
        }
        // delegate to parent to dissociate
        currentParty.removeFromAgreements(this);
    }

    private AgreementRoleType type;

    @Title(sequence = "1")
    @MemberOrder(sequence = "3")
    public AgreementRoleType getType() {
        return type;
    }

    public void setType(final AgreementRoleType type) {
        this.type = type;
    }

    private LocalDate startDate;

    @MemberOrder(sequence = "4")
    @Optional
    @javax.jdo.annotations.Persistent
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private LocalDate endDate;

    @MemberOrder(sequence = "5")
    @Optional
    @javax.jdo.annotations.Persistent
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // {{ Comparable impl
    /**
     * This is necessary but not sufficient; in
     * {@link Agreement#addRole(Party, AgreementRoleType, LocalDate, LocalDate)}
     * there is logic to ensure that there cannot be two {@link AgreementRole
     * actor}s of the same type at the same point in time.
     * 
     * TODO: need to implement the above statement!!!
     */
    @Override
    public int compareTo(AgreementRole other) {
        return ORDERING_BY_TYPE.compound(ORDERING_BY_START_DATE).compare(this, other);
    }

    public static Ordering<AgreementRole> ORDERING_BY_TYPE = new Ordering<AgreementRole>() {
        public int compare(AgreementRole p, AgreementRole q) {
            return AgreementRoleType.ORDERING_BY_TITLE.nullsFirst().compare(p.getType(), q.getType());
        }
    };
    public static Ordering<AgreementRole> ORDERING_BY_START_DATE = new Ordering<AgreementRole>() {
        public int compare(AgreementRole p, AgreementRole q) {
            return Orderings.LOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };

    // }}

    public boolean isCurrent() {
        return isActiveOn(clockService.now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    @Programmatic // excluded form the metamodel
    public LocalDateInterval getInterval() {
        return new LocalDateInterval(getStartDate(), getEndDate(), IntervalEnding.INCLUDING_END_DATE);
    }

    // {{ injected: ClockService
    private ClockService clockService;

    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }
    // }}

}

//public class Lease {
//enum IntervalDistinguisher {
//    CONTRACT,
//    TENANCY
//}
//
//   public LocalDateInterval getInterval(IntervalDistinguisher d) {
//       if (d==IntervalDistinguisher.CONTRACT) {
//           return new LocalDateInterval(getContractStartDate(), getContractEndDate());
//       }
//   }
//
//   
//}

interface Intervalled {
    LocalDate getStartDate();
    LocalDate getEndDate();
    @Programmatic
    LocalDateInterval getInterval(Object distinguisher);
}



//main() {
//    
//    
//    Lease l;
//    
//    LocalDateInterval ldi = l.getInterval(CONTRACT);
//}
