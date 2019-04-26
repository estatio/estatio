package org.estatio.module.turnover.dom;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.core.commons.lang.ArrayExtensions;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.party.dom.Person;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "TurnoverReportingConfig_occupancy_UNQ", members = "occupancy")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.TurnoverReportingConfig "
                        + "WHERE occupancy == :occupancy "),
        @javax.jdo.annotations.Query(
                name = "findByStartDateOnOrBefore", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.TurnoverReportingConfig "
                        + "WHERE startDate <= :date"),
        @javax.jdo.annotations.Query(
                name = "findByReporter", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.TurnoverReportingConfig "
                        + "WHERE reporter == :reporter "),
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.TurnoverReportingConfig"
)
public class TurnoverReportingConfig extends UdoDomainObject2<Turnover> {

    public TurnoverReportingConfig(){
        super("occupancy");
    }

    public TurnoverReportingConfig(
            final Occupancy occupancy,
            final Person reporter,
            final LocalDate startDate,
            final Frequency prelimFrequency,
            final Frequency auditedFrequency,
            final Currency currency
    ){
        this();
        this.occupancy = occupancy;
        this.reporter = reporter;
        this.startDate = startDate;
        this.prelimFrequency = prelimFrequency;
        this.auditedFrequency = auditedFrequency;
        this.currency = currency;
    }

    @Getter @Setter
    @Column(name = "occupancyId", allowsNull = "false")
    private Occupancy occupancy;

    @Getter @Setter
    @Column(name = "personId", allowsNull = "true")
    private Person reporter;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Frequency prelimFrequency;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Frequency auditedFrequency;

    @Getter @Setter
    @Column(name = "currencyId", allowsNull = "false")
    private Currency currency;

    @Action(semantics = SemanticsOf.SAFE)
    public LocalDate getEndDate(){
        return ArrayExtensions.coalesce(occupancy.getEndDate(), occupancy.getLease().getTenancyEndDate(), occupancy.getLease().getEndDate());
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getOccupancy().getApplicationTenancy();
    }

    public void produceEmptyTurnovers(final LocalDate date) {
        if (!date.isBefore(getStartDate())) {
            if (prelimFrequency.hasStartDate(date)) turnoverRepository.createNewEmpty(getOccupancy(), date, Type.PRELIMINARY, getPrelimFrequency(), getCurrency());
            if (auditedFrequency.hasStartDate(date)) turnoverRepository.createNewEmpty(getOccupancy(), date, Type.AUDITED, getAuditedFrequency(), getCurrency());
        }
    }

    @Inject
    TurnoverRepository turnoverRepository;
}
