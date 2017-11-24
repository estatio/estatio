package org.estatio.module.party.dom.relationship;

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
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.types.DescriptionType;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithInterval;

import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(name = "findByParty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.relationship.PartyRelationship "
                        + "WHERE (to == :party || from == :party) "),
        @Query(name = "findByFromAndTypeAndBetweenStartDateAndEndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.relationship.PartyRelationship "
                        + "WHERE (from == :from) "  // in brackets so that 'from' is parsed as field rather than keyword
                        + "   && relationshipType == :relationshipType "
                        + "   && (startDate == null || startDate <= :date) "
                        + "   && (endDate == null   || endDate   >= :date) "
        )
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.party.relationship.PartyRelationship"
)
public class PartyRelationship extends AbstractDomainObject implements WithInterval<PartyRelationship> {

    public String title() {
        return String.format("%s is %s of %s",
                getFrom().getName(),
                getRelationshipType().fromTitle(),
                getTo().getName());
    }

    public PartyRelationship(Party fromParty, Party toParty, PartyRelationshipTypeEnum relaionshipType) {
        setFrom(fromParty);
        setTo(toParty);
        setRelationshipType(relaionshipType);
    }


    @Column(name = "fromPartyId", allowsNull = "false")
    @Getter @Setter
    private Party from;


    @Column(name = "toPartyId", allowsNull = "false")
    @Getter @Setter
    private Party to;


    @Column(allowsNull = "false")
    @Getter @Setter
    private PartyRelationshipTypeEnum relationshipType;


    @Persistent
    @Getter @Setter
    private LocalDate startDate;


    @Persistent
    @Getter @Setter
    private LocalDate endDate;


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

    public PartyRelationship changeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        setStartDate(startDate);
        setEndDate(endDate);
        return this;
    }

    public LocalDate default0ChangeDates() {
        return getStartDate();
    }

    public LocalDate default1ChangeDates() {
        return getEndDate();
    }

    public String validateChangeDates(final LocalDate startDate, final LocalDate endDate) {
        if (!new LocalDateInterval(startDate, endDate).isValid()) {
            return "Start or end date is not valid";
        }
        return null;
    }

    // //////////////////////////////////////

    @Column(allowsNull = "true", length = DescriptionType.Meta.MAX_LEN)
    @Getter @Setter
    private String description;

    public PartyRelationship changeDescription(
            final String description) {
        setDescription(description);
        return this;
    }

    public String default0ChangeDescription() {
        return getDescription();
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
