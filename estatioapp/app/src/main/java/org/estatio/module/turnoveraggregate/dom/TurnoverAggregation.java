package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.types.MoneyType;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;

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
@Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnoveraggregate.dom.TurnoverAggregation "
                        + "WHERE turnoverReportingConfig == :turnoverReportingConfig "
                        + "&& date == :date "),
        @Query(
                name = "findByTurnoverReportingConfig", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnoveraggregate.dom.TurnoverAggregation "
                        + "WHERE turnoverReportingConfig == :turnoverReportingConfig "),
})
@Unique(name = "TurnoverAggregation_turnoverReportingConfig_date_UNQ", members = { "turnoverReportingConfig", "date" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnoveraggregate.dom.TurnoverAggregation"
)
public class TurnoverAggregation {

    public TurnoverAggregation(){}

    public TurnoverAggregation(final TurnoverReportingConfig turnoverReportingConfig, final LocalDate date, final Currency currency){
        this.turnoverReportingConfig = turnoverReportingConfig;
        this.date = date;
        this.currency = currency;
    }

    /*
    * Date
    * LeaseDetailKey
    * CurrencyKey
    */

    @Getter @Setter
    @Column(name = "turnoverReportingConfigId", allowsNull = "false")
    private TurnoverReportingConfig turnoverReportingConfig;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate date;

    @Getter @Setter
    @Column(name = "currencyId", allowsNull = "false")
    private Currency currency;

    /*
     * TurnoverGrossAmount1MCY
     * TurnoverNetAmount1MCY
     * TurnoverNonComparableFlag1MCY
     * TurnoverCount1MCY
     * TurnoverGrossAmount1MPY
     * TurnoverNetAmount1MPY
     * TurnoverNonComparableFlag1MPY
     * TurnoverCount1MPY
     * IsComparable1M
     */

    @Getter @Setter
    @Column(name = "turnoverAggregate1MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod aggregate1Month;

    /*
     * TurnoverGrossAmount2MCY
     * TurnoverNetAmount2MCY
     * TurnoverNonComparableFlag2MCY
     * TurnoverCount2MCY
     * TurnoverGrossAmount2MPY
     * TurnoverNetAmount2MPY
     * TurnoverNonComparableFlag2MPY
     * TurnoverCount2MPY
     * IsComparable2M
     */

    @Getter @Setter
    @Column(name = "turnoverAggregate2MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod aggregate2Month;

    /*
     * TurnoverGrossAmount3MCY
     * TurnoverNetAmount3MCY
     * TurnoverNonComparableFlag3MCY
     * TurnoverCount3MCY
     * TurnoverGrossAmount3MPY
     * TurnoverNetAmount3MPY
     * TurnoverNonComparableFlag3MPY
     * TurnoverCount3MPY
     * IsComparable3M
     */

    @Getter @Setter
    @Column(name = "turnoverAggregate3MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod aggregate3Month;

    /*
     * TurnoverGrossAmount6MCY
     * TurnoverNetAmount6MCY
     * TurnoverNonComparableFlag6MCY
     * TurnoverCount6MCY
     * TurnoverGrossAmount6MPY
     * TurnoverNetAmount6MPY
     * TurnoverNonComparableFlag6MPY
     * TurnoverCount6MPY
     * IsComparable6M
     */

    @Getter @Setter
    @Column(name = "turnoverAggregate6MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod aggregate6Month;

    /*
     * TurnoverGrossAmount9MCY
     * TurnoverNetAmount9MCY
     * TurnoverNonComparableFlag9MCY
     * TurnoverCount9MCY
     * TurnoverGrossAmount9MPY
     * TurnoverNetAmount9MPY
     * TurnoverNonComparableFlag9MPY
     * TurnoverCount9MPY
     * IsComparable9M
     */

    @Getter @Setter
    @Column(name = "turnoverAggregate9MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod aggregate9Month;

    /*
     * TurnoverGrossAmount12MCY
     * TurnoverNetAmount12MCY
     * TurnoverNonComparableFlag12MCY
     * TurnoverCount12MCY
     * TurnoverGrossAmount12MPY
     * TurnoverNetAmount12MPY
     * TurnoverNonComparableFlag12MPY
     * TurnoverCount12MPY
     * IsComparable12M
     */

    @Getter @Setter
    @Column(name = "turnoverAggregate12MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod aggregate12Month;

    /*
     * TurnoverGrossAmountYTDCY
     * TurnoverNetAmountYTDCY
     * TurnoverNonComparableFlagYTDCY
     * TurnoverCountYTDCY
     * TurnoverGrossAmountYTDPY
     * TurnoverNetAmountYTDPY
     * TurnoverNonComparableFlagYTDPY
     * TurnoverCountYTDPY
     * IsComparableYTD
     */

    @Getter @Setter
    @Column(name = "turnoverAggregateToDateId", allowsNull = "false")
    private TurnoverAggregateToDate aggregateToDate;

    /*
     * TurnoverGrossAmount1MCY_1
     * TurnoverNetAmount1MCY_1
     * TurnoverGrossAmount1MCY_2
     * TurnoverNetAmount1MCY_2
     */

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal grossAmount1MCY_1;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal netAmount1MCY_1;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal grossAmount1MCY_2;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal netAmount1MCY_2;

    /*
     * TurnoverComments12MCY
     * TurnoverComments12MPY
     */

    @Getter @Setter
    @Column(allowsNull = "true", length = 1024)
    private String comments12MCY;

    @Getter @Setter
    @Column(allowsNull = "true", length = 1024)
    private String comments12MPY;

    /*
     * TurnoverPurchaseCount1MCY
     * TurnoverPurchaseCount1MPY
     * IsComparableTPC1M
     */

    @Getter @Setter
    @Column(name = "purchaseCountAggregate1MonthId", allowsNull = "false")
    private PurchaseCountAggregateForPeriod purchaseCountAggregate1Month;

    /*
     * TurnoverPurchaseCount3MCY
     * TurnoverPurchaseCount3MPY
     * IsComparableTPC3M
     */

    @Getter @Setter
    @Column(name = "purchaseCountAggregate3MonthId", allowsNull = "false")
    private PurchaseCountAggregateForPeriod purchaseCountAggregate3Month;

    /*
     * TurnoverPurchaseCount6MCY
     * TurnoverPurchaseCount6MPY
     * IsComparableTPC6M
     */

    @Getter @Setter
    @Column(name = "purchaseCountAggregate6MonthId", allowsNull = "false")
    private PurchaseCountAggregateForPeriod purchaseCountAggregate6Month;

    /*
     * TurnoverPurchaseCount12MCY
     * TurnoverPurchaseCount12MPY
     * IsComparableTPC12M
     */

    @Getter @Setter
    @Column(name = "purchaseCountAggregate12MonthId", allowsNull = "false")
    private PurchaseCountAggregateForPeriod purchaseCountAggregate12Month;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDateTime calculatedOn;

    @Persistent(table="AggregationsTurnovers")
    @Join(column="aggregationId")
    @Element(column="turnoverId")
    @Getter
    @Setter
    public Set<Turnover> turnovers = new HashSet<>();

    @Programmatic
    public void remove() {

        // remove
        TurnoverAggregateForPeriod p1 = getAggregate1Month();
        TurnoverAggregateForPeriod p2 = getAggregate2Month();
        TurnoverAggregateForPeriod p3 = getAggregate3Month();
        TurnoverAggregateForPeriod p4 = getAggregate6Month();
        TurnoverAggregateForPeriod p5 = getAggregate9Month();
        TurnoverAggregateForPeriod p6 = getAggregate12Month();

        TurnoverAggregateToDate td = getAggregateToDate();

        PurchaseCountAggregateForPeriod c1 = getPurchaseCountAggregate1Month();
        PurchaseCountAggregateForPeriod c2 = getPurchaseCountAggregate3Month();
        PurchaseCountAggregateForPeriod c3 = getPurchaseCountAggregate6Month();
        PurchaseCountAggregateForPeriod c4 = getPurchaseCountAggregate12Month();

        repositoryService.removeAndFlush(this);

        // clean up
        if (p1!=null) p1.remove();
        if (p2!=null) p2.remove();
        if (p3!=null) p3.remove();
        if (p4!=null) p4.remove();
        if (p5!=null) p5.remove();
        if (p6!=null) p6.remove();

        if (td!=null) td.remove();

        if (c1!=null) c1.remove();
        if (c2!=null) c2.remove();
        if (c3!=null) c3.remove();
        if (c4!=null) c4.remove();

    }

    @Programmatic
    public LocalDateInterval calculationPeriod(){
        return LocalDateInterval.including(getDate().minusMonths(23), getDate());
    }

    @Inject RepositoryService repositoryService;
}
