package org.estatio.module.turnover.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.MoneyType;
import org.incode.module.base.dom.types.NotesType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.turnover.dom.entry.TurnoverEntryService;

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
                name = "Turnover_config_date_type_UNQ", members = {"config", "date", "type"})
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE config == :config "
                        + "&& date == :date "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByConfigAndTypeAndFrequencyAndStatusBeforeDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE config == :config "
                        + "&& type == :type "
                        + "&& frequency == :frequency "
                        + "&& status == :status "
                        + "&& date < :threshold "
                        + "ORDER BY date DESC "),
        @javax.jdo.annotations.Query(
                name = "findByConfig", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE config == :config "
                        + "ORDER BY date DESC "),
        @javax.jdo.annotations.Query(
                name = "findByConfigAndStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE config == :config "
                        + "&& status == :status "
                        + "ORDER BY date DESC "),
        @javax.jdo.annotations.Query(
                name = "findByConfigAndTypeAndStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE config == :config "
                        + "&& type == :type "
                        + "&& status == :status "
                        + "ORDER BY date DESC "),
        @javax.jdo.annotations.Query(
                name = "findByConfigAndTypeAndDateAndStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE config == :config "
                        + "&& type == :type "
                        + "&& status == :status "
                        + "&& date == :date "),
        @javax.jdo.annotations.Query(
                name = "findByConfigAndTypeAndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE config == :config "
                        + "&& type == :type "
                        + "&& date == :date "),
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.Turnover"
)
public class Turnover extends UdoDomainObject2<Turnover> {

    public Turnover(){
        super("config, date, type");
    }

    public Turnover(
            final TurnoverReportingConfig config,
            final LocalDate date,
            final Type type,
            final Frequency frequency,
            final Currency currency,
            final Status status){
        this();
        this.config = config;
        this.date = date;
        this.type = type;
        this.frequency = frequency;
        this.currency = currency;
        this.status = status;
    }

    public Turnover(
            final TurnoverReportingConfig config,
            final LocalDate date,
            final Type type,
            final Frequency frequency,
            final Status status,
            final LocalDateTime reportedAt,
            final String reportedBy,
            final Currency currency,
            final BigDecimal netAmount,
            final BigDecimal grossAmount,
            final BigInteger purchaseCount,
            final String comments,
            final boolean nonComparable){
        this();
        this.config = config;
        this.date = date;
        this.type = type;
        this.frequency = frequency;
        this.status = status;
        this.reportedAt = reportedAt;
        this.reportedBy = reportedBy;
        this.currency = currency;
        this.netAmount = netAmount;
        this.grossAmount = grossAmount;
        this.purchaseCount = purchaseCount;
        this.comments = comments;
        this.nonComparable = nonComparable;
    }

    public String title() {
       return TitleBuilder.start()
               .withName(getDate())
               .withName(getConfig().getOccupancy().getUnit().getName())
               .withName(getConfig().getOccupancy().getLease().getReference())
               .toString();
    }

    @Getter @Setter
    @Column(name = "turnoverReportingConfigId", allowsNull = "false")
    private TurnoverReportingConfig config;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate date;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Type type;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Frequency frequency;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Status status;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDateTime reportedAt;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String reportedBy;

    @Getter @Setter
    @Column(name = "currencyId", allowsNull = "false")
    private Currency currency;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal netAmount;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal grossAmount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigInteger purchaseCount;

    @Getter @Setter
    @Column(allowsNull = "true", length = NotesType.Meta.MAX_LEN)
    private String comments;

    @Getter @Setter
    @Column(allowsNull = "false")
    private boolean nonComparable;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Turnover> getPrevious(){
        return turnoverRepository.findApprovedByConfigAndTypeAndFrequencyBeforeDate(getConfig(), getType(),getFrequency(), getDate());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Occupancy getOccupancy(){
        return getConfig().getOccupancy();
    }

    @Programmatic
    public Turnover nextNew() {
        return turnoverEntryService.nextNewForReporter(personRepository.me(), this);
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getConfig().getApplicationTenancy();
    }

    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverEntryService turnoverEntryService;

    @Inject PersonRepository personRepository;

}
