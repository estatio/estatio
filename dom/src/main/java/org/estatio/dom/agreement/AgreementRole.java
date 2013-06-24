package org.estatio.dom.agreement;

import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
        name = "findByAgreementAndPartyAndTypeAndStartDate", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.agreement.AgreementRole " +
        		"WHERE agreement == :agreement " +
        		"&& party == :party " +
        		"&& type == :type " +
        		"&& startDate == :startDate"),
	@javax.jdo.annotations.Query(
        name = "findByAgreementAndTypeAndContainsDate", language = "JDOQL", 
        value = "SELECT " +
                "FROM org.estatio.dom.agreement.AgreementRole " +
                "WHERE agreement == :agreement " +
                "&& type == :type "+ 
                "&& (startDate == null | startDate < :date) "+
                "&& (endDate == null | endDate > :date) ")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class AgreementRole extends EstatioTransactionalObject<AgreementRole> implements WithInterval {

    public AgreementRole() {
        super("agreement, party, startDate desc, type");
    }

    // //////////////////////////////////////

    private Agreement agreement;

    @Title(sequence = "3", prepend = ":")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(final Agreement agreement) {
        this.agreement = agreement;
    }

    public void modifyAgreement(final Agreement agreement) {
        Agreement currentAgreement = getAgreement();
        // check for no-op
        if (agreement == null || agreement.equals(currentAgreement)) {
            return;
        }
        // delegate to parent to associate
        if (currentAgreement != null) {
            currentAgreement.removeFromRoles(this);
        }
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

    // //////////////////////////////////////

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

    // //////////////////////////////////////

    private AgreementRoleType type;

    @Title(sequence = "1")
    @MemberOrder(sequence = "3")
    public AgreementRoleType getType() {
        return type;
    }

    public void setType(final AgreementRoleType type) {
        this.type = type;
    }

    // //////////////////////////////////////

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

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "6")
    public boolean isCurrent() {
        return isActiveOn(clockService.now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return getInterval().contains(localDate);
    }


    // //////////////////////////////////////

    private ClockService clockService;

    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }

}
