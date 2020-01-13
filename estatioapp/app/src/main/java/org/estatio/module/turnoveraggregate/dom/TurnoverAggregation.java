package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.Type;

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
        this.calculated = false;
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
    @Column(allowsNull = "true")
    private BigDecimal grossAmount1MCY_1;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal netAmount1MCY_1;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal grossAmount1MCY_2;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal netAmount1MCY_2;

    /*
     * TurnoverComments12MCY
     * TurnoverComments12MPY
     */

    @Getter @Setter
    @Column(allowsNull = "true")
    private String comments12MCY;

    @Getter @Setter
    @Column(allowsNull = "true")
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
    @Column(allowsNull = "false")
    private boolean calculated;

    @Programmatic
    public TurnoverAggregation calculate(final List<Turnover> turnovers){
        turnoverAggregationService.calculateTurnoverAggregation(this, turnovers);
        return this;
    }

    @Programmatic
    public void remove() {
        // clean up
        if (getAggregate1Month()!=null) getAggregate1Month().remove();
        if (getAggregate2Month()!=null) getAggregate2Month().remove();
        if (getAggregate3Month()!=null) getAggregate3Month().remove();
        if (getAggregate6Month()!=null) getAggregate6Month().remove();
        if (getAggregate9Month()!=null) getAggregate9Month().remove();
        if (getAggregate12Month()!=null) getAggregate12Month().remove();
        if (getAggregateToDate()!=null) getAggregateToDate().remove();
        if (getPurchaseCountAggregate1Month()!=null) getPurchaseCountAggregate1Month().remove();
        if (getPurchaseCountAggregate3Month()!=null) getPurchaseCountAggregate3Month().remove();
        if (getPurchaseCountAggregate6Month()!=null) getPurchaseCountAggregate6Month().remove();
        if (getPurchaseCountAggregate12Month()!=null) getPurchaseCountAggregate12Month().remove();
        // remove
        repositoryService.removeAndFlush(this);
    }

    @Inject
    TurnoverAggregationService turnoverAggregationService;

    @Inject RepositoryService repositoryService;
}
