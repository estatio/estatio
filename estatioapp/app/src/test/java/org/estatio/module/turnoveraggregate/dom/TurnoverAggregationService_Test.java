package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

public class TurnoverAggregationService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock TurnoverRepository mockTurnoverRepo;

    @Test
    public void aggregateForPeriod_works() {

        // given
        Turnover t1 = new Turnover();
        t1.setGrossAmount(new BigDecimal("123.00"));
        t1.setNetAmount(new BigDecimal("111.11"));
        Turnover t2 = new Turnover();
        t2.setGrossAmount(new BigDecimal("0.45"));
        Turnover tpy1 = new Turnover();
        tpy1.setGrossAmount(new BigDecimal("100.23"));
        tpy1.setNetAmount(new BigDecimal("99.99"));

        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override List<Turnover> turnoversToAggregateForPeriod(
                    final Occupancy occupancy, final LocalDate date, final AggregationPeriod aggregationPeriod, final Type type, final Frequency frequency, boolean prevYear) {
                return prevYear ? Arrays.asList(tpy1) : Arrays.asList(t1, t2);
            }
        };

        TurnoverAggregateForPeriod aggregateForPeriod = new TurnoverAggregateForPeriod();
        final Occupancy occupancy = new Occupancy();
        final LocalDate aggregationDate = new LocalDate(2019, 1, 1);
        aggregateForPeriod.setAggregationPeriod(AggregationPeriod.P_2M);

        // when
        service.aggregateForPeriod(aggregateForPeriod, occupancy, aggregationDate, Type.PRELIMINARY, Frequency.MONTHLY);

        // then
        Assertions.assertThat(aggregateForPeriod.getTurnoverCount()).isEqualTo(2);
        Assertions.assertThat(aggregateForPeriod.getTurnoverCountPreviousYear()).isEqualTo(1);

        Assertions.assertThat(aggregateForPeriod.getGrossAmount()).isEqualTo(new BigDecimal("123.45"));
        Assertions.assertThat(aggregateForPeriod.getNetAmount()).isEqualTo(new BigDecimal("111.11"));
        Assertions.assertThat(aggregateForPeriod.getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("100.23"));
        Assertions.assertThat(aggregateForPeriod.getNetAmountPreviousYear()).isEqualTo(new BigDecimal("99.99"));

        Assertions.assertThat(aggregateForPeriod.isNonComparableThisYear()).isEqualTo(false);
        Assertions.assertThat(aggregateForPeriod.isNonComparablePreviousYear()).isEqualTo(false);
        Assertions.assertThat(aggregateForPeriod.isComparable()).isEqualTo(false);

    }

    @Test
    public void aggregateToDate_works() {

        // given
        final LocalDate aggregationDate = new LocalDate(2019, 2, 1);
        Turnover t1 = new Turnover();
        t1.setGrossAmount(new BigDecimal("123.00"));
        t1.setNetAmount(new BigDecimal("111.11"));
        Turnover t2 = new Turnover();
        t2.setGrossAmount(new BigDecimal("0.45"));
        Turnover tpy1 = new Turnover();
        tpy1.setGrossAmount(new BigDecimal("100.23"));
        tpy1.setNetAmount(new BigDecimal("99.99"));

        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override
            List<Turnover> turnoversToAggregate(
                    final Occupancy occupancy,
                    final LocalDate date,
                    final LocalDate periodStartDate,
                    final LocalDate periodEndDate,
                    final Type type,
                    final Frequency frequency) {
                // this year returns t1, t2, previous year tpy1
                return periodEndDate.equals(aggregationDate) ? Arrays.asList(t1, t2) : Arrays.asList(tpy1);
            }
        };

        TurnoverAggregateToDate turnoverAggregateToDate = new TurnoverAggregateToDate();
        final Occupancy occupancy = new Occupancy();

        // when
        service.aggregateToDate(turnoverAggregateToDate, occupancy, aggregationDate, Type.PRELIMINARY, Frequency.MONTHLY);

        // then
        Assertions.assertThat(turnoverAggregateToDate.getTurnoverCount()).isEqualTo(2);
        Assertions.assertThat(turnoverAggregateToDate.getTurnoverCountPreviousYear()).isEqualTo(1);

        Assertions.assertThat(turnoverAggregateToDate.getGrossAmount()).isEqualTo(new BigDecimal("123.45"));
        Assertions.assertThat(turnoverAggregateToDate.getNetAmount()).isEqualTo(new BigDecimal("111.11"));
        Assertions.assertThat(turnoverAggregateToDate.getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("100.23"));
        Assertions.assertThat(turnoverAggregateToDate.getNetAmountPreviousYear()).isEqualTo(new BigDecimal("99.99"));

        Assertions.assertThat(turnoverAggregateToDate.isNonComparableThisYear()).isEqualTo(false);
        Assertions.assertThat(turnoverAggregateToDate.isNonComparablePreviousYear()).isEqualTo(false);
        Assertions.assertThat(turnoverAggregateToDate.isComparable()).isEqualTo(false);

    }

    @Test
    public void aggregateForPurchaseCount_works() {

        // given
        Turnover t1 = new Turnover();
        t1.setPurchaseCount(new BigInteger("123"));
        Turnover t2 = new Turnover();
        t2.setPurchaseCount(new BigInteger("234"));
        Turnover tpy1 = new Turnover();
        tpy1.setPurchaseCount(new BigInteger("345"));

        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override List<Turnover> turnoversToAggregateForPeriod(
                    final Occupancy occupancy, final LocalDate date, final AggregationPeriod aggregationPeriod, final Type type, final Frequency frequency, boolean prevYear) {
                return prevYear ? Arrays.asList(tpy1) : Arrays.asList(t1, t2);
            }
        };

        PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod = new PurchaseCountAggregateForPeriod();
        final Occupancy occupancy = new Occupancy();
        final LocalDate aggregationDate = new LocalDate(2019, 1, 1);
        purchaseCountAggregateForPeriod.setAggregationPeriod(AggregationPeriod.P_2M);

        // when
        service.aggregateForPurchaseCount(purchaseCountAggregateForPeriod, occupancy, aggregationDate, Type.PRELIMINARY, Frequency.MONTHLY);

        // then
        Assertions.assertThat(purchaseCountAggregateForPeriod.getCount()).isEqualTo(new BigInteger("357"));
        Assertions.assertThat(purchaseCountAggregateForPeriod.getCountPreviousYear()).isEqualTo(new BigInteger("345"));
        Assertions.assertThat(purchaseCountAggregateForPeriod.isComparable()).isEqualTo(false);

    }

    @Test
    public void containsNonComparableTurnover_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when
        List<Turnover> turnoverList = Arrays.asList();
        // then
        Assertions.assertThat(service.containsNonComparableTurnover(turnoverList)).isFalse();

        // when
        Turnover t1 = new Turnover();
        t1.setNonComparable(false);
        turnoverList = Arrays.asList(t1);
        // then
        Assertions.assertThat(service.containsNonComparableTurnover(turnoverList)).isFalse();

        // when
        Turnover t2 = new Turnover();
        t2.setNonComparable(true);
        turnoverList = Arrays.asList(t1, t2);
        // then
        Assertions.assertThat(service.containsNonComparableTurnover(turnoverList)).isTrue();

    }

    @Test
    public void isComparable_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when, then
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, false, false)).isTrue();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 3, 2, false, false)).isTrue();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 1, 2, false, false)).isFalse();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 1, false, false)).isFalse();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, true, false)).isFalse();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, false, true)).isFalse();

        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 0, 2, false, true)).isFalse();
    }

    @Test
    public void isComparableToDate_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2019, 2, 1);

        // when, then
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 2, 2, false, false)).isTrue();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 3, 2, false, false)).isTrue();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 1, 2, false, false)).isFalse();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 2, 1, false, false)).isFalse();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 2, 2, true, false)).isFalse();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 2, 2, false, true)).isFalse();

        Assertions.assertThat(service.isComparableToDate(aggregationDate, 0, 2, false, true)).isFalse();
    }

    @Test
    public void getMinNumberOfTurnoversToDate_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when, then
        Assertions.assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,1,1))).isEqualTo(1);
        Assertions.assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,2,1))).isEqualTo(2);
        //etc
        Assertions.assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,12,1))).isEqualTo(12);

    }

    @Test
    public void leasesToExamine_works() throws Exception {
        //given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when
        Lease l1 = new Lease();
        Lease l2 = new Lease();
        Lease l3 = new Lease();
        l2.setPrevious(l3);
        l1.setPrevious(l2);

        // then
        Assertions.assertThat(service.leasesToExamine(l3)).hasSize(1);
        Assertions.assertThat(service.leasesToExamine(l3)).contains(l3);
        Assertions.assertThat(service.leasesToExamine(l2)).hasSize(2);
        Assertions.assertThat(service.leasesToExamine(l3)).doesNotContain(l1);
        Assertions.assertThat(service.leasesToExamine(l1)).hasSize(3);
    }

    @Test
    public void occupanciesToExamine_works() throws Exception {

        List<Lease> leases;

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();
        Unit sameUnit = new Unit();
        Unit otherUnit = new Unit();

        // when
        leases = new ArrayList<>();
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(0);

        // when
        Lease l1 = new Lease();
        Occupancy occOnSameUnit1 = new Occupancy();
        occOnSameUnit1.setUnit(sameUnit);
        Occupancy occOnSameUnit2 = new Occupancy();
        occOnSameUnit2.setUnit(sameUnit);
        occOnSameUnit2.setStartDate(new LocalDate(2019,1,1)); // to differentiate in sorted set
        Occupancy occOnOtherUnit1 = new Occupancy();
        occOnOtherUnit1.setUnit(otherUnit);
        l1.getOccupancies().addAll(Arrays.asList(occOnSameUnit1, occOnSameUnit2, occOnOtherUnit1));
        leases = Arrays.asList(l1);
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(2);
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).doesNotContain(occOnOtherUnit1);

        // when no occupancies on same sameUnit and exactly one occupancy on other sameUnit
        Lease l2 = new Lease();
        Occupancy occOnOtherUnit2 = new Occupancy();
        occOnOtherUnit2.setUnit(otherUnit);
        l2.getOccupancies().add(occOnOtherUnit2);
        leases = Arrays.asList(l2);
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(1);

        // when multiple leases
        leases = Arrays.asList(l1, l2);
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(3);
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).doesNotContain(occOnOtherUnit1);

        // when no occupancies on same sameUnit and multiple occupancies on other sameUnit
        Lease l3 = new Lease();
        Occupancy occOnOtherUnit3 = new Occupancy();
        occOnOtherUnit3.setUnit(otherUnit);
        Occupancy occOnOtherUnit4 = new Occupancy();
        occOnOtherUnit4.setUnit(otherUnit);
        occOnOtherUnit4.setStartDate(new LocalDate(2019,1,1)); // to differentiate in sorted set
        l3.getOccupancies().addAll(Arrays.asList(occOnOtherUnit3, occOnOtherUnit4));
        leases = Arrays.asList(l3);
        // then
        Assertions.assertThat(leases.get(0).getOccupancies()).hasSize(2);
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(0);

    }
}