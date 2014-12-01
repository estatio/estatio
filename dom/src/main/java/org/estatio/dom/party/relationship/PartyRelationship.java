package org.estatio.dom.party.relationship;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithInterval;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(name = "findByParty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.relationship.PartyRelationship "
                        + "WHERE (to == :party || from == :party) ")
})
public class PartyRelationship extends AbstractDomainObject implements WithInterval<PartyRelationship> {

    public String title() {
        return String.format("%s is %s of %s",
                getFrom().getName(),
                getRelationshipType().fromTitle(),
                getTo().getName());
    }

    public PartyRelationship(Party fromParty, Party toParty, PartyRelationshipType relaionshipType) {
        setFrom(fromParty);
        setTo(toParty);
        setRelationshipType(relaionshipType);
    }

    // //////////////////////////////////////

    private Party from;

    @Column(name = "fromPartyId", allowsNull = "false")
    @MemberOrder(sequence = "1")
    public Party getFrom() {
        return from;
    }

    public void setFrom(Party from) {
        this.from = from;
    }

    // //////////////////////////////////////

    private Party to;

    @Column(name = "toPartyId", allowsNull = "false")
    @MemberOrder(sequence = "2")
    public Party getTo() {
        return to;
    }

    public void setTo(Party to) {
        this.to = to;
    }

    // //////////////////////////////////////

    private PartyRelationshipType relationshipType;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "3")
    public PartyRelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(PartyRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Override
    @Persistent
    @MemberOrder(sequence = "4")
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    private LocalDate endDate;

    @Override
    @Persistent
    @MemberOrder(sequence = "5")
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public LocalDateInterval getInterval() {
        return new LocalDateInterval(getStartDate(), getEndDate());
    }

    @Override
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    @Override
    public boolean isCurrent() {
        return getInterval().contains(clockService.now());
    }

    // //////////////////////////////////////

    private String description;

    @Column(allowsNull = "true", length = JdoColumnLength.DESCRIPTION)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    @Programmatic
    public void doRemove() {
        getContainer().remove(this);
    }

    // //////////////////////////////////////

    @Inject
    private ClockService clockService;

}
