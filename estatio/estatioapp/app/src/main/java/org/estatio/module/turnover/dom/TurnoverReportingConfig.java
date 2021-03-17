package org.estatio.module.turnover.dom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.estatio.module.turnover.contributions.Occupancy_createEmptyTurnovers;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.party.dom.Person;
import org.estatio.module.turnover.dom.aggregation.AggregationPattern;

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
@Indices({
        @Index(
                name = "TurnoverReportingConfig_occupancy_type_frequency_IDX",
                members = {"occupancy", "type", "frequency"}),
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
        @javax.jdo.annotations.Query(
                name = "findByOccupancyAndTypeAndFrequency", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.TurnoverReportingConfig "
                        + "WHERE occupancy == :occupancy && "
                        + "type == :type && "
                        + "frequency == :frequency"),
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.TurnoverReportingConfig"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class TurnoverReportingConfig extends UdoDomainObject2<Turnover> {

    public TurnoverReportingConfig(){
        super("occupancy, type");
    }

    public String title(){
        TitleBuffer buffer = new TitleBuffer();
        buffer.append(getOccupancy());
        buffer.append(getType());
        buffer.append(getFrequency());
        return buffer.toString();
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

    @Getter @Setter
    @Column(allowsNull = "true")
    private AggregationPattern aggregationPattern;

    @Getter @Setter
    @Column(allowsNull = "false")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private Boolean aggregationInitialized;

    @Action(semantics = SemanticsOf.SAFE)
    public LocalDate getEndDate(){

        LocalDate endDateToUse = occupancy.getEffectiveEndDate();
        if (endDateToUse==null) return null;

        return endDateToUse.isAfter(getStartDate()) ? endDateToUse : getStartDate(); // ECP-962: prevents bad occupancy and / or lease data to produce wrong illegal interval on turnover reporting config
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getOccupancy().getApplicationTenancy();
    }

    public TurnoverReportingConfig produceEmptyTurnover(final LocalDate date) {
        if (isActiveOnDate(date) && !getOccupancy().getReportTurnover().equals(Occupancy.OccupancyReportingType.NO)) {
            if (frequency.hasStartDate(date)) turnoverRepository.createNewEmpty(this, date, getType(), getFrequency(), getCurrency());
        }
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public TurnoverReportingConfig changeStartDate(final LocalDate date) {
        List<Turnover> newTurnovers = turnoverRepository.findByConfigWithStatusNew(this);
        newTurnovers.stream().forEach(turnover -> repositoryService.remove(turnover));
        setStartDate(date);
        factoryService.mixin(Occupancy_createEmptyTurnovers.class, occupancy).$$(date, clockService.now());
        return this;
    }

    public String disableChangeStartDate() {
        if (getTurnovers().stream().anyMatch(turnover -> turnover.getStatus()== Status.APPROVED)) {
            return "Cannot change start date when there are already turnovers reported";
        }
        return null;
    }

    @Programmatic
    public List<Person> allTurnoverReporters(){
        List<Person> result = new ArrayList<>();
        List<FixedAssetRole> roles = fixedAssetRoleRepository.findAllForProperty(occupancy.getUnit().getProperty());
        result.addAll(
                roles.stream()
                .filter(r->r.getType()==FixedAssetRoleTypeEnum.TURNOVER_REPORTER)
                .filter(r -> r.getStartDate() == null || r.getStartDate().isBefore(clockService.now().plusDays(1)))
                .filter(r -> r.getEndDate() == null || r.getEndDate().isAfter(clockService.now().minusDays(1)))
                .map(r->r.getParty())
                .filter(p->p.getClass().isAssignableFrom(Person.class))
                .map(Person.class::cast)
                .collect(Collectors.toList())
        );
        if (getReporter()!=null) result.add(getReporter());
        return result;
    }

    public List<Turnover> getTurnovers(){
        return turnoverRepository.findByConfig(this);
    }

    @Programmatic
    public boolean isActiveOnDate(final LocalDate date){
        LocalDate startDateToUse = getEffectiveStartDate();
        LocalDateInterval interval = LocalDateInterval.including(startDateToUse, getEndDate());
        return interval.contains(date);
    }

    @Programmatic
    public LocalDate getEffectiveStartDate() {
        LocalDate startDateToUse;
        switch (getFrequency()){

            case MONTHLY:
                startDateToUse = new LocalDate(getStartDate().getYear(), getStartDate().getMonthOfYear(), 1);
            break;

            case YEARLY:
                startDateToUse = new LocalDate(getStartDate().getYear(), 1, 1);
            break;

            default:
                startDateToUse = getStartDate();
        }
        return startDateToUse;
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

    @Inject
    RepositoryService repositoryService;

    @Inject
    FactoryService factoryService;

}
