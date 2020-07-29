package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;

public class AggregationViewModelHelperService_Test {

    @Test
    public void helperCurrentYear(){

        // given
        AggregationViewModelHelperService service = new AggregationViewModelHelperService();
        TurnoverAggregateForPeriod aggregateForPeriod = new TurnoverAggregateForPeriod();

        // when
        String resultString = service.helperAggregateForPeriod(aggregateForPeriod, false);
        // then
        Assertions.assertThat(resultString).isEqualTo("Gross --- | Net ---");

        // and when
        final String string = expectedAmountString(aggregateForPeriod, "1.23", "1.01");
        resultString = service.helperAggregateForPeriod(aggregateForPeriod, false);
        // then
        Assertions.assertThat(resultString).isEqualTo(string);

    }

    @Test
    public void helperPreviousYear(){

        // given
        AggregationViewModelHelperService service = new AggregationViewModelHelperService();
        TurnoverAggregateForPeriod aggregateForPeriod = new TurnoverAggregateForPeriod();

        // when
        String resultString = service.helperAggregateForPeriod(aggregateForPeriod, true);
        // then
        Assertions.assertThat(resultString).isEqualTo("Gross --- | Net ---");

        // and when
        final String string = expectedAmountStringPY(aggregateForPeriod, "1.24", "1.02");
        resultString = service.helperAggregateForPeriod(aggregateForPeriod, true);
        // then
        Assertions.assertThat(resultString).isEqualTo(string);

    }

    @Test
    public void helperComparable() throws Exception {

        // given
        AggregationViewModelHelperService service = new AggregationViewModelHelperService();
        TurnoverAggregateForPeriod aggregateForPeriod = new TurnoverAggregateForPeriod();

        // when
        String resultString = service.helperComparable(aggregateForPeriod);
        // then
        Assertions.assertThat(resultString).isEqualTo("non comparable");

        // and when
        aggregateForPeriod.setComparable(true);
        resultString = service.helperComparable(aggregateForPeriod);
        // then
        Assertions.assertThat(resultString).isEqualTo("comparable");

    }

    @Test
    public void helperCountCurrentYear() throws Exception {

        // given
        AggregationViewModelHelperService service = new AggregationViewModelHelperService();
        PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod = new PurchaseCountAggregateForPeriod();

        // when
        String resultString = service.helperCount(purchaseCountAggregateForPeriod, false);
        // then
        Assertions.assertThat(resultString).isEqualTo("---");

        // and when
        purchaseCountAggregateForPeriod.setCount(BigInteger.valueOf(123));
        resultString = service.helperCount(purchaseCountAggregateForPeriod, false);
        // then
        Assertions.assertThat(resultString).isEqualTo("123");

    }

    @Test
    public void helperCountPreviousYear() throws Exception {

        // given
        AggregationViewModelHelperService service = new AggregationViewModelHelperService();
        PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod = new PurchaseCountAggregateForPeriod();

        // when
        String resultString = service.helperCount(purchaseCountAggregateForPeriod, true);
        // then
        Assertions.assertThat(resultString).isEqualTo("---");

        // and when
        purchaseCountAggregateForPeriod.setCountPreviousYear(BigInteger.valueOf(234));
        resultString = service.helperCount(purchaseCountAggregateForPeriod, true);
        // then
        Assertions.assertThat(resultString).isEqualTo("234");

    }

    @Test
    public void helperCountComparable() throws Exception {

        // given
        AggregationViewModelHelperService service = new AggregationViewModelHelperService();
        PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod = new PurchaseCountAggregateForPeriod();

        // when
        String resultString = service.helperComparable(purchaseCountAggregateForPeriod);
        // then
        Assertions.assertThat(resultString).isEqualTo("non comparable");

        // and when
        purchaseCountAggregateForPeriod.setComparable(true);
        resultString = service.helperComparable(purchaseCountAggregateForPeriod);
        // then
        Assertions.assertThat(resultString).isEqualTo("comparable");

    }

    @Test
    public void helperLeaseInvolved() throws Exception {

        // given
        AggregationViewModelHelperService service = new AggregationViewModelHelperService();
        TurnoverAggregation aggregation = new TurnoverAggregation();

        // when
        String resultString = service.helperLeaseInvolved(aggregation, null);
        // then
        Assertions.assertThat(resultString).isEqualTo("---");

        // and when (no turnovers)
        TurnoverAggregateForPeriod aggregateForPeriod = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }
        };
        resultString = service.helperLeaseInvolved(aggregation, aggregateForPeriod);
        // then
        Assertions.assertThat(resultString).isEqualTo("---");

        // and when (turnovers, 1 occ)
        final String leasRef123 = "LeasRef123";
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        final Occupancy occupancy = new Occupancy();
        final Lease lease = new Lease();
        lease.setReference(leasRef123);
        occupancy.setLease(lease);
        config.setOccupancy(occupancy);
        Turnover turnover = new Turnover(config, null, null, null, null, null);
        TurnoverAggregateForPeriod aggregateForPeriodWithTo1 = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList(turnover);
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }
        };
        resultString = service.helperLeaseInvolved(aggregation, aggregateForPeriodWithTo1);
        // then
        Assertions.assertThat(resultString).isEqualTo("LeasRef123");

        // and when (turnovers, 2 occs)
        final String leasRef234 = "LeasRef234";
        TurnoverReportingConfig config2 = new TurnoverReportingConfig();
        final Occupancy occupancy2 = new Occupancy();
        final Lease lease2 = new Lease();
        lease2.setReference(leasRef234);
        occupancy2.setLease(lease2);
        config2.setOccupancy(occupancy2);
        Turnover turnover2 = new Turnover(config2, null, null, null, null, null);
        TurnoverAggregateForPeriod aggregateForPeriodWithTo2 = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList(turnover2);
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList(turnover);
            }
        };
        resultString = service.helperLeaseInvolved(aggregation, aggregateForPeriodWithTo2);
        // then
        Assertions.assertThat(resultString).isEqualTo("LeasRef234 | LeasRef123");
    }

    @Test
    public void getLines() {

        // given
        AggregationViewModelHelperService service = new AggregationViewModelHelperService();
        TurnoverAggregation aggregation = new TurnoverAggregation();
        TurnoverAggregateForPeriod A1M = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }
        };
        TurnoverAggregateForPeriod A2M = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }
        };
        TurnoverAggregateForPeriod A3M = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }
        };
        TurnoverAggregateForPeriod A6M = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }
        };
        TurnoverAggregateForPeriod A9M = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }
        };
        TurnoverAggregateForPeriod A12M = new TurnoverAggregateForPeriod(){
            @Override public List<Turnover> getTurnovers(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }

            @Override
            public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation) {
                return Arrays.asList();
            }
        };
        PurchaseCountAggregateForPeriod P1M = new PurchaseCountAggregateForPeriod();
        PurchaseCountAggregateForPeriod P3M = new PurchaseCountAggregateForPeriod();
        PurchaseCountAggregateForPeriod P6M = new PurchaseCountAggregateForPeriod();
        PurchaseCountAggregateForPeriod P12M = new PurchaseCountAggregateForPeriod();

        aggregation.setAggregate1Month(A1M);
        aggregation.setAggregate2Month(A2M);
        aggregation.setAggregate3Month(A3M);
        aggregation.setAggregate6Month(A6M);
        aggregation.setAggregate9Month(A9M);
        aggregation.setAggregate12Month(A12M);
        aggregation.setPurchaseCountAggregate1Month(P1M);
        aggregation.setPurchaseCountAggregate3Month(P3M);
        aggregation.setPurchaseCountAggregate6Month(P6M);
        aggregation.setPurchaseCountAggregate12Month(P12M);

        // when
        List<AggregationViewModelLine> lines = service.getLines(aggregation);

        // then
        Assertions.assertThat(lines).hasSize(7);
        final String emptyAmount = "Gross --- | Net ---";
        final String emptyString = "---";

        assertLine(lines.get(0), "turnover", emptyAmount, emptyAmount, emptyAmount, emptyAmount, emptyAmount, emptyAmount);
        assertLine(lines.get(1), "turnover previous year", emptyAmount, emptyAmount, emptyAmount, emptyAmount, emptyAmount, emptyAmount);
        assertLine(lines.get(2), "turnover comparable", "non comparable", "non comparable", "non comparable", "non comparable", "non comparable", "non comparable");
        assertLine(lines.get(3), "purchase count", emptyString, "", emptyString, emptyString, "", emptyString);
        assertLine(lines.get(4), "purchase count previous year", emptyString, "", emptyString, emptyString, "", emptyString);
        assertLine(lines.get(5), "purchase count comparable", "non comparable", "", "non comparable", "non comparable", "", "non comparable");
        assertLine(lines.get(6), "leases involved", emptyString, emptyString, emptyString, emptyString, emptyString, emptyString);

        // and when
        String a1m = expectedAmountString(A1M, "1.23", "1.00");
        String a1mP = expectedAmountStringPY(A1M, "1.24", "1.01");
        P1M.setCount(BigInteger.valueOf(123));
        P1M.setCountPreviousYear(BigInteger.valueOf(234));
        P1M.setComparable(true);
        lines = service.getLines(aggregation);

        // then
        assertLine(lines.get(0), "turnover", a1m, emptyAmount, emptyAmount, emptyAmount, emptyAmount, emptyAmount);
        assertLine(lines.get(1), "turnover previous year", a1mP, emptyAmount, emptyAmount, emptyAmount, emptyAmount, emptyAmount);
        assertLine(lines.get(2), "turnover comparable", "comparable", "non comparable", "non comparable", "non comparable", "non comparable", "non comparable");
        assertLine(lines.get(3), "purchase count", "123", "", emptyString, emptyString, "", emptyString);
        assertLine(lines.get(4), "purchase count previous year", "234", "", emptyString, emptyString, "", emptyString);
        assertLine(lines.get(5), "purchase count comparable", "comparable", "", "non comparable", "non comparable", "", "non comparable");

        // and when
        A2M.setComparable(true);
        lines = service.getLines(aggregation);
        assertLine(lines.get(2), "turnover comparable", "comparable", "comparable", "non comparable", "non comparable", "non comparable", "non comparable");

        // and when
        A3M.setComparable(true);
        P3M.setComparable(true);
        lines = service.getLines(aggregation);
        assertLine(lines.get(2), "turnover comparable", "comparable", "comparable", "comparable", "non comparable", "non comparable", "non comparable");
        assertLine(lines.get(5), "purchase count comparable", "comparable", "", "comparable", "non comparable", "", "non comparable");

        // and when
        A6M.setComparable(true);
        P6M.setComparable(true);
        lines = service.getLines(aggregation);
        assertLine(lines.get(2), "turnover comparable", "comparable", "comparable", "comparable", "comparable", "non comparable", "non comparable");
        assertLine(lines.get(5), "purchase count comparable", "comparable", "", "comparable", "comparable", "", "non comparable");

        // and when
        A9M.setComparable(true);
        lines = service.getLines(aggregation);
        assertLine(lines.get(2), "turnover comparable", "comparable", "comparable", "comparable", "comparable", "comparable", "non comparable");
        assertLine(lines.get(5), "purchase count comparable", "comparable", "", "comparable", "comparable", "", "non comparable");

        // and when
        A12M.setComparable(true);
        P12M.setComparable(true);
        lines = service.getLines(aggregation);
        assertLine(lines.get(2), "turnover comparable", "comparable", "comparable", "comparable", "comparable", "comparable", "comparable");
        assertLine(lines.get(5), "purchase count comparable", "comparable", "", "comparable", "comparable", "", "comparable");

    }

    private String expectedAmountString(TurnoverAggregateForPeriod aggregate, final String gross, final String net){
        aggregate.setGrossAmount(new BigDecimal(gross));
        aggregate.setNetAmount(new BigDecimal(net));
        return "Gross " + gross + " | Net " + net;
    }

    private String expectedAmountStringPY(TurnoverAggregateForPeriod aggregate, final String gross, final String net){
        aggregate.setGrossAmountPreviousYear(new BigDecimal(gross));
        aggregate.setNetAmountPreviousYear(new BigDecimal(net));
        aggregate.setComparable(true);
        return "Gross " + gross + " | Net " + net;
    }

    private void assertLine(final AggregationViewModelLine line, final String title, final String M1, final String M2, final String M3, final String M6, final String M9, final String M12){
        Assertions.assertThat(line.getTitle()).isEqualTo(title);
        Assertions.assertThat(line.getAgg1M()).isEqualTo(M1);
        Assertions.assertThat(line.getAgg2M()).isEqualTo(M2);
        Assertions.assertThat(line.getAgg3M()).isEqualTo(M3);
        Assertions.assertThat(line.getAgg6M()).isEqualTo(M6);
        Assertions.assertThat(line.getAgg9M()).isEqualTo(M9);
        Assertions.assertThat(line.getAgg12M()).isEqualTo(M12);
    }
}