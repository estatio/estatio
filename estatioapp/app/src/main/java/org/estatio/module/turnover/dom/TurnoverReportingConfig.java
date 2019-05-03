package org.estatio.module.turnover.dom;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.core.commons.lang.ArrayExtensions;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
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
                name = "TurnoverReportingConfig_occupancy_type_UNQ", members = { "occupancy", "type" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.TurnoverReportingConfig "
                        + "WHERE occupancy == :occupancy && type == :type "),
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
        @javax.jdo.annotations.Query(
                name = "findByOccupancy", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.TurnoverReportingConfig "
                        + "WHERE occupancy == :occupancy "),
        @javax.jdo.annotations.Query(
                name = "findByOccupancyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.TurnoverReportingConfig "
                        + "WHERE occupancy == :occupancy && "
                        + "type == :type"),
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.TurnoverReportingConfig"
)
public class TurnoverReportingConfig extends UdoDomainObject2<Turnover> {

    public TurnoverReportingConfig(){
        super("occupancy, type");
    }

    public TurnoverReportingConfig(
            final Occupancy occupancy,
            final Type type,
            final Person reporter,
            final LocalDate startDate,
            final Frequency frequency,
            final Currency currency
    ){
        this();
        this.occupancy = occupancy;
        this.type = type;
        this.reporter = reporter;
        this.startDate = startDate;
        this.frequency = frequency;
        this.currency = currency;
    }

    @Getter @Setter
    @Column(name = "occupancyId", allowsNull = "false")
    private Occupancy occupancy;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Type type;

    @Getter @Setter
    @Column(name = "reporterPersonId", allowsNull = "true")
    private Person reporter;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Frequency frequency;

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
        if (isActiveOnDate(date)) {
            if (frequency.hasStartDate(date)) turnoverRepository.createNewEmpty(this, date, getType(), getFrequency(), getCurrency());
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Person effectiveReporter(){
        return ArrayExtensions.coalesce(getReporter(), deriveReporterFromOccupancy(getOccupancy()));
    }

    @Programmatic
    public LocalDateInterval getInterval(){
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }


    @Programmatic
    public boolean isActiveOnDate(final LocalDate date){
        return getInterval().contains(date);
    }

    @Programmatic
    Person deriveReporterFromOccupancy(final Occupancy occupancy) {
        final Person reporterToUse;
        List<FixedAssetRole> roles = fixedAssetRoleRepository.findAllForProperty(occupancy.getUnit().getProperty());
        FixedAssetRole derivedRoleFromOccupancy = roles.stream()
                .filter(r->r.getType()==FixedAssetRoleTypeEnum.TURNOVER_REPORTER)
                .filter(r -> r.getStartDate() == null || r.getStartDate().isBefore(clockService.now().plusDays(1)))
                .filter(r -> r.getEndDate() == null || r.getEndDate().isAfter(clockService.now().minusDays(1)))
                .findFirst().orElse(null);
        reporterToUse = derivedRoleFromOccupancy !=null ? (Person) derivedRoleFromOccupancy.getParty() : null;
        return reporterToUse;
    }

    @Inject
    TurnoverRepository turnoverRepository;

    @Inject FixedAssetRoleRepository fixedAssetRoleRepository;

}
