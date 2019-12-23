package org.estatio.module.turnover.dom.aggregate;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Type;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.aggregate.TurnoverAggregation"
)
public class TurnoverAggregation {

    /*
    * Date
    * LeaseDetailKey
    * CurrencyKey
    */

    @Getter @Setter
    @Column(name = "occupancyId", allowsNull = "false")
    private Occupancy occupancy;

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
    @Column(name = "aggregate1MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod Aggregate1Month;

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
    @Column(name = "aggregate2MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod Aggregate2Month;

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
    @Column(name = "aggregate3MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod Aggregate3Month;

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
    @Column(name = "aggregate6MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod Aggregate6Month;

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
    @Column(name = "aggregate9MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod Aggregate9Month;

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
    @Column(name = "aggregate12MonthId", allowsNull = "false")
    private TurnoverAggregateForPeriod Aggregate12Month;

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

}
