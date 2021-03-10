package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.client.util.Lists;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class TurnoverAggregationService {

    public static Logger LOG = LoggerFactory.getLogger(TurnoverAggregationService.class);

    public static boolean guard(final Type type, final Frequency frequency, final String msgType, final String msgFreq) {
        if (type != Type.PRELIMINARY) {
            LOG.warn(String.format(msgType,
                    type));
            return true;
        }
        if (frequency != Frequency.MONTHLY) {
            LOG.warn(String.format(msgFreq,
                    frequency));
            return true;
        }
        return false;
    }

    // TODO: candidate for configuration property?
    public static LocalDate MIN_AGGREGATION_DATE = new LocalDate(2010, 1,1);

    public void aggregate(
            final Turnover changedTurnover
    ){
        final TurnoverReportingConfig config = changedTurnover.getConfig();
        final Lease lease = config.getOccupancy().getLease();
        if (!config.getAggregationInitialized() && lease.getPrevious()!=null){
            //ECP-1245: in order to avoid possible 'gaps' in aggregation creation when previous turnovers on previous lease
            aggregateTurnoversForLease(lease, config.getEffectiveStartDate(), null, false);
        }
        aggregate(changedTurnover.getDate(), config, changedTurnover.getReportedAt(), false);
    }

    public void aggregateTurnoversForLease(final Lease lease, final LocalDate startDate, final LocalDate endDate, final boolean maintainOnly){
        //since we analyze all previous and next leases with all associated configs, any config with type prelimninary and frequency monthly will do
        TurnoverReportingConfig firstConfigCandidate = null;
        for (Occupancy o : lease.getOccupancies()){
            final TurnoverReportingConfig c = turnoverReportingConfigRepository
                    .findByOccupancyAndTypeAndFrequency(o, Type.PRELIMINARY, Frequency.MONTHLY).stream().findFirst()
                    .orElse(null);
            if (c!=null && firstConfigCandidate==null ){
                firstConfigCandidate = c;
            }
        }

        if (firstConfigCandidate==null) return;

        LocalDate startDateToUse = startDate==null || startDate.isBefore(MIN_AGGREGATION_DATE) ? MIN_AGGREGATION_DATE.minusMonths(23).withDayOfMonth(1) : startDate.withDayOfMonth(1);
        LocalDate endDateToUse = endDate==null ? clockService.now().withDayOfMonth(1).plusMonths(23) : endDate.withDayOfMonth(1);

        if (endDateToUse.isBefore(startDateToUse)) return;

        // since we look 24 months ahead the aggregations to be made are
        List<LocalDate> turnoverDates = new ArrayList<>();
        turnoverDates.add(startDateToUse.withDayOfMonth(1));
        LocalDate d = startDateToUse.plusMonths(24);
        while (!d.isAfter(endDateToUse)){
            turnoverDates.add(d.withDayOfMonth(1));
            d = d.plusMonths(24);
        }

        if (turnoverDates.isEmpty()) return;

        if (maintainOnly){
            aggregate(turnoverDates.get(0), firstConfigCandidate, null, true);
        } else {
            for (LocalDate toDate : turnoverDates) {
                aggregate(toDate, firstConfigCandidate, null, maintainOnly);
            }
        }

    }

    public void aggregate(final LocalDate turnoverDate, final TurnoverReportingConfig config, final LocalDateTime changedAt, final boolean maintainOnly){

        if (guard(config.getType(), config.getFrequency(), "No aggregate-turnovers-for-lease implementation for type %s found.",
                "No aggregate-turnovers-for-lease for frequency %s found."))
            return;

        LocalDate turnoverDateToUse = turnoverDate==null ? MIN_AGGREGATION_DATE.minusMonths(23): turnoverDate;

        // a changed turnover has impact on aggregations of it's month and the next 23 months; an aggregation looks back 24 months
        final LocalDateInterval turnoverSelectionPeriod = LocalDateInterval.excluding(
                turnoverDateToUse.withDayOfMonth(1).minusMonths(24),
                turnoverDateToUse.withDayOfMonth(1).plusMonths(24)
        );
        // this is the period we calculate aggregations for
        final LocalDateInterval calculationPeriod = LocalDateInterval.excluding(turnoverDateToUse.withDayOfMonth(1), turnoverDateToUse.withDayOfMonth(1).plusMonths(24));

        // Process step 1: analyze  ////////////////////////////////////////////////////////////////////////////////////
        config.getOccupancy().getLease();
        final List<AggregationAnalysisReportForConfig> analysisReports = turnoverAnalysisService.analyze(config.getOccupancy().getLease(), config.getType(),
                config.getFrequency());


        // Process step 2: maintain  ///////////////////////////////////////////////////////////////////////////////////
        analysisReports.stream().forEach(r -> {
            // 2a: determine and set strategy
            final TurnoverReportingConfig c = r.getTurnoverReportingConfig();
            c.setAggregationPattern(r.getAggregationPattern());

            // 2b: create / delete aggregations
            maintainTurnoverAggregationsForConfig(r);
        });

        config.setAggregationInitialized(true);

        if (maintainOnly)
            return;

        // Proces step 3: calculate
        List<TurnoverReportingConfig> configsToCalculate = selectConfigsWithDateInOrBeforeSelectionPeriod(analysisReports, turnoverSelectionPeriod);
        List<Turnover> turnoverSelectionForCalculations = new ArrayList<>();
        configsToCalculate.forEach(c->{
            turnoverSelectionForCalculations.addAll(
                    c.getTurnovers()
                            .stream()
                            .filter(t->t.getStatus() == Status.APPROVED)
                            .filter(t -> turnoverSelectionPeriod.contains(t.getDate()))
                            .collect(Collectors.toList())
            );

        });
        List<TurnoverAggregation> aggregationsToCalculate = selectAggregationsToCalculate(analysisReports, calculationPeriod);
        aggregationsToCalculate.forEach(a->{
            // dirty checking -- NOTE: ECP-1201 when importing turnovers we now always replace Turnover#createdAt by the timestamp of the upload, so this may be of no effect at all in practice when the selection of aggregations to calculate is tight
            if (a.getCalculatedOn()==null || changedAt==null || changedAt.isAfter(a.getCalculatedOn())) {
                calculateAggregation(a, turnoverSelectionForCalculations, analysisReports);
            }
        });

    }

    /**
     * This method returns a list of Turnover Reporting Configurations that makes a rough selection of those possibly involved by including all in and before the calculation period
     * @param reports
     * @param calculationPeriod
     * @return
     */
    List<TurnoverReportingConfig> selectConfigsWithDateInOrBeforeSelectionPeriod(final List<AggregationAnalysisReportForConfig> reports, final LocalDateInterval calculationPeriod){
        List<TurnoverReportingConfig> result = new ArrayList<>();
        reports.forEach(r-> {
            if (!r.getTurnoverReportingConfig().getEffectiveStartDate().isAfter(calculationPeriod.endDate())){
                result.add(r.getTurnoverReportingConfig());
            }
        });
        return result;
    }

    List<TurnoverAggregation> selectAggregationsToCalculate(final List<AggregationAnalysisReportForConfig> reports, final LocalDateInterval calculationPeriod){
        List<TurnoverAggregation> result = new ArrayList<>();
        reports.forEach(r->{
            r.getAggregationDates().forEach(d->{
                if (calculationPeriod.contains(d)){
                    result.add(turnoverAggregationRepository.findOrCreate(r.getTurnoverReportingConfig(), d, r.getTurnoverReportingConfig().getCurrency()));
                }
            });
        });
        return result;
    }

    public void calculateAggregation(final TurnoverAggregation aggregation, final List<Turnover> turnovers, final List<AggregationAnalysisReportForConfig> reports){

        final AggregationAnalysisReportForConfig report = reports.stream()
                .filter(r -> r.getTurnoverReportingConfig().equals(aggregation.getTurnoverReportingConfig()))
                .findFirst().orElse(null);
        if (report==null) return; // Should not happen

        // from turnovers offered, select those that are within the calculation period for the aggregation and are on a config that we aggregate for
        final List<Turnover> toSelection = turnovers.stream()
                .filter(t-> report.getConfigsToIncludeInAggregation().contains(t.getConfig()))
                .filter(t -> aggregation.calculationPeriod().contains(t.getDate()))
                .collect(Collectors.toList());

        // ECP-1211 adds aggregate12MonthCovid which can be null for aggregations already present at the time
        if (aggregation.getAggregate12MonthCovid()==null) aggregation.setAggregate12MonthCovid(turnoverAggregateForPeriodRepository.create(AggregationPeriod.P_12M_COVID));
        aggregation.getTurnovers().clear();
        aggregation.getTurnovers().addAll(toSelection);

        calculateAggregationForOther(aggregation, toSelection);

        aggregation.getAggregate1Month().calculate(aggregation, toSelection);
        aggregation.getAggregate2Month().calculate(aggregation, toSelection);
        aggregation.getAggregate3Month().calculate(aggregation, toSelection);
        aggregation.getAggregate6Month().calculate(aggregation, toSelection);
        aggregation.getAggregate9Month().calculate(aggregation, toSelection);
        aggregation.getAggregate12Month().calculate(aggregation, toSelection);
        aggregation.getAggregate12MonthCovid().calculate(aggregation, toSelection);

        aggregation.getAggregateToDate().calculate(aggregation, toSelection);

        aggregation.getPurchaseCountAggregate1Month().calculate(aggregation, toSelection);
        aggregation.getPurchaseCountAggregate3Month().calculate(aggregation, toSelection);
        aggregation.getPurchaseCountAggregate6Month().calculate(aggregation, toSelection);
        aggregation.getPurchaseCountAggregate12Month().calculate(aggregation, toSelection);

        aggregation.setCalculatedOn(clockService.nowAsLocalDateTime());

    }

    void calculateAggregationForOther(final TurnoverAggregation aggregation, final List<Turnover> turnovers){

        // reset
        aggregation.setGrossAmount1MCY_1(null);
        aggregation.setNetAmount1MCY_1(null);
        aggregation.setGrossAmount1MCY_2(null);
        aggregation.setNetAmount1MCY_2(null);
        aggregation.setComments12MCY(null);
        aggregation.setComments12MPY(null);

        LocalDate aggDate = aggregation.getDate();
        List<Turnover> toList = turnovers.stream()
                .filter(t -> t.getDate().equals(aggDate.minusMonths(1))).collect(Collectors.toList());
        toList.forEach(t->{
            aggregation.setGrossAmount1MCY_1(aggAmount(aggregation.getGrossAmount1MCY_1(), t.getGrossAmount()));
            aggregation.setNetAmount1MCY_1(aggAmount(aggregation.getNetAmount1MCY_1(), t.getNetAmount()));
        });

        toList = turnovers.stream()
                .filter(t -> t.getDate().equals(aggDate.minusMonths(2))).collect(Collectors.toList());
        toList.forEach(t->{
            aggregation.setGrossAmount1MCY_2(aggAmount(aggregation.getGrossAmount1MCY_2(), t.getGrossAmount()));
            aggregation.setNetAmount1MCY_2(aggAmount(aggregation.getNetAmount1MCY_2(), t.getNetAmount()));
        });

        LocalDateInterval I12MCY = LocalDateInterval.including(aggDate.minusMonths(11), aggDate);
        LocalDateInterval I12MPY = LocalDateInterval.including(aggDate.minusYears(1).minusMonths(11), aggDate.minusYears(1));
        String ccy = null;
        String cpy = null;
        for (Turnover t : turnovers){
            if (t.getComments()!=null && !t.getComments().equals("") && I12MCY.contains(t.getDate())){
                if (ccy==null ){
                    ccy = t.getComments();
                } else {
                    ccy = ccy.concat(" | ").concat(t.getComments());
                }
            }
            if (t.getComments()!=null && !t.getComments().equals("") && I12MPY.contains(t.getDate())){
                if (cpy==null ){
                    cpy = t.getComments();
                } else {
                    cpy = cpy.concat(" | ").concat(t.getComments());
                }
            }
        }
        aggregation.setComments12MCY(ccy!=null && ccy.length()>1022 ? ccy.substring(0,1022) : ccy);
        aggregation.setComments12MPY(cpy!=null && cpy.length()>1022 ? cpy.substring(0,1022) : cpy);

    }

    void calculateTurnoverAggregateForPeriod(
            final TurnoverAggregateForPeriod turnoverAggregateForPeriod,
            final TurnoverAggregation aggregation,
            final List<Turnover> turnovers) {

        final LocalDate aggregationDate = aggregation.getDate();
        final List<Turnover> toCY = getTurnoversForAggregateForPeriod(turnoverAggregateForPeriod, aggregationDate, turnovers, false);
        final List<Turnover> toPY = getTurnoversForAggregateForPeriod(turnoverAggregateForPeriod, aggregationDate, turnovers, true);

        if (toCY.isEmpty() && toPY.isEmpty()) return;

        resetTurnoverAggregateForPeriod(turnoverAggregateForPeriod);

        toCY.forEach(t->{
            turnoverAggregateForPeriod.setGrossAmount(aggAmount(turnoverAggregateForPeriod.getGrossAmount(), t.getGrossAmount()));
            turnoverAggregateForPeriod.setNetAmount(aggAmount(turnoverAggregateForPeriod.getNetAmount(), t.getNetAmount()));
            turnoverAggregateForPeriod.setTurnoverCount(determineTurnoverCount(toCY, aggregation.getTurnoverReportingConfig().getFrequency()));
        });
        if (toCY.isEmpty()){
            turnoverAggregateForPeriod.setTurnoverCount(null);
            turnoverAggregateForPeriod.setNonComparableThisYear(null);
        } else {
            if (turnoverAggregateForPeriod.getTurnoverCount()==null) turnoverAggregateForPeriod.setTurnoverCount(0);
            turnoverAggregateForPeriod.setNonComparableThisYear(containsNonComparableTurnover(toCY));
        }

        toPY.forEach(t->{
            turnoverAggregateForPeriod.setGrossAmountPreviousYear(aggAmount(turnoverAggregateForPeriod.getGrossAmountPreviousYear(), t.getGrossAmount()));
            turnoverAggregateForPeriod.setNetAmountPreviousYear(aggAmount(turnoverAggregateForPeriod.getNetAmountPreviousYear(), t.getNetAmount()));
            turnoverAggregateForPeriod.setTurnoverCountPreviousYear(determineTurnoverCount(toPY, aggregation.getTurnoverReportingConfig().getFrequency()));
        });
        if (toPY.isEmpty()){
            turnoverAggregateForPeriod.setTurnoverCountPreviousYear(null);
            turnoverAggregateForPeriod.setNonComparablePreviousYear(null);
        } else {
            if (turnoverAggregateForPeriod.getTurnoverCountPreviousYear()==null) turnoverAggregateForPeriod.setTurnoverCountPreviousYear(0);
            turnoverAggregateForPeriod.setNonComparablePreviousYear(containsNonComparableTurnover(toPY));
        }

        turnoverAggregateForPeriod.setComparable(isComparableForPeriod(
                turnoverAggregateForPeriod.getAggregationPeriod(),
                turnoverAggregateForPeriod.getTurnoverCount(),
                turnoverAggregateForPeriod.getTurnoverCountPreviousYear(),
                turnoverAggregateForPeriod.getNonComparableThisYear(),
                turnoverAggregateForPeriod.getNonComparablePreviousYear()
                ));

    }

    Integer determineTurnoverCount(final List<Turnover> turnovers, final Frequency frequency){
        if (frequency!=Frequency.MONTHLY) return null; // not implemented
        List<LocalDate> firstDatesOfMonth = new ArrayList<>();
        turnovers.forEach(t->{
            if ((t.getGrossAmount()!=null && t.getGrossAmount().compareTo(BigDecimal.ZERO) > 0) || (t.getNetAmount()!=null && t.getNetAmount().compareTo(BigDecimal.ZERO) > 0)) {
                final LocalDate firstDayOfMonthTurnoverDate = t.getDate().withDayOfMonth(1);
                if (!firstDatesOfMonth.contains(firstDayOfMonthTurnoverDate)) {
                    firstDatesOfMonth.add(firstDayOfMonthTurnoverDate);
                }
            }
        });
        return firstDatesOfMonth.isEmpty() ? null : firstDatesOfMonth.size();
    }

    List<Turnover> getTurnoversForAggregateForPeriod(
            final TurnoverAggregateForPeriod turnoverAggregateForPeriod,
            final TurnoverAggregation aggregation,
            final boolean previousYear) {
        return getTurnoversForAggregateForPeriod(turnoverAggregateForPeriod, aggregation.getDate(), aggregation.getTurnovers().stream().collect(
                Collectors.toList()), previousYear);
    }

    List<Turnover> getTurnoversForAggregateForPeriod(
            final TurnoverAggregateForPeriod turnoverAggregateForPeriod,
            final LocalDate aggregationDate,
            final List<Turnover> turnovers,
            final boolean previousYear){
        return getTurnoversForAggregationPeriod(turnoverAggregateForPeriod.getAggregationPeriod(), aggregationDate, turnovers, previousYear);
    }

    public void calculateTurnoverAggregateToDate(
            final TurnoverAggregateToDate turnoverAggregateToDate,
            final TurnoverAggregation aggregation,
            final List<Turnover> turnovers){

        final LocalDate aggregationDate = aggregation.getDate();
        final List<Turnover> toCY = getTurnoversForAggregateToDate(aggregationDate, turnovers, false);
        final List<Turnover> toPY = getTurnoversForAggregateToDate(aggregationDate, turnovers, true);
        if (toCY.isEmpty() && toPY.isEmpty()) return;

        resetTurnoverAggregateToDate(turnoverAggregateToDate);

        toCY.forEach(t->{
            turnoverAggregateToDate.setGrossAmount(aggAmount(turnoverAggregateToDate.getGrossAmount(), t.getGrossAmount()));
            turnoverAggregateToDate.setNetAmount(aggAmount(turnoverAggregateToDate.getNetAmount(), t.getNetAmount()));
            turnoverAggregateToDate.setTurnoverCount(determineTurnoverCount(toCY, aggregation.getTurnoverReportingConfig().getFrequency()));
        });
        if (toCY.isEmpty()){
            turnoverAggregateToDate.setTurnoverCount(null);
            turnoverAggregateToDate.setNonComparableThisYear(null);
        } else {
            if (turnoverAggregateToDate.getTurnoverCount()==null) turnoverAggregateToDate.setTurnoverCount(0);
            turnoverAggregateToDate.setNonComparableThisYear(containsNonComparableTurnover(toCY));
        }

        toPY.forEach(t->{
            turnoverAggregateToDate.setGrossAmountPreviousYear(aggAmount(turnoverAggregateToDate.getGrossAmountPreviousYear(), t.getGrossAmount()));
            turnoverAggregateToDate.setNetAmountPreviousYear(aggAmount(turnoverAggregateToDate.getNetAmountPreviousYear(), t.getNetAmount()));
            turnoverAggregateToDate.setTurnoverCountPreviousYear(determineTurnoverCount(toPY, aggregation.getTurnoverReportingConfig().getFrequency()));
        });
        if (toPY.isEmpty()){
            turnoverAggregateToDate.setTurnoverCountPreviousYear(null);
            turnoverAggregateToDate.setNonComparablePreviousYear(null);
        } else {
            if (turnoverAggregateToDate.getTurnoverCountPreviousYear()==null) turnoverAggregateToDate.setTurnoverCountPreviousYear(0);
            turnoverAggregateToDate.setNonComparablePreviousYear(containsNonComparableTurnover(toPY));
        }

        turnoverAggregateToDate.setComparable(isComparableToDate(
                aggregationDate,
                turnoverAggregateToDate.getTurnoverCount(),
                turnoverAggregateToDate.getTurnoverCountPreviousYear(),
                turnoverAggregateToDate.getNonComparableThisYear(),
                turnoverAggregateToDate.getNonComparablePreviousYear()
        ));
    }

    List<Turnover> getTurnoversForAggregateToDate(
            final TurnoverAggregation aggregation,
            final boolean previousYear) {
        return getTurnoversForAggregateToDate(aggregation.getDate(), aggregation.getTurnovers().stream().collect(
                Collectors.toList()), previousYear);
    }

    List<Turnover> getTurnoversForAggregateToDate(
            final LocalDate aggregationDate,
            final List<Turnover> turnovers,
            final boolean previousYear){
        final LocalDate startOfTheYear = new LocalDate(aggregationDate.getYear(), 1, 1);
        final LocalDateInterval interval = LocalDateInterval.including(previousYear ? startOfTheYear.minusYears(1) : startOfTheYear, previousYear ? aggregationDate.minusYears(1) : aggregationDate);
        return turnovers.stream().filter(t->interval.contains(t.getDate())).collect(Collectors.toList());
    }

    public void calculatePurchaseCountAggregateForPeriod(
            final PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod,
            final LocalDate aggregationDate,
            final List<Turnover> turnovers) {

        final List<Turnover> toCY = getTurnoversForPurchaseCountAggregateForPeriod(purchaseCountAggregateForPeriod, aggregationDate, turnovers, false);
        final List<Turnover> toPY = getTurnoversForPurchaseCountAggregateForPeriod(purchaseCountAggregateForPeriod, aggregationDate, turnovers, true);

        if (toCY.isEmpty() && toPY.isEmpty()) return;

        resetPurchaseCountAggregateForPeriod(purchaseCountAggregateForPeriod);

        Integer numberOfCountsCY = null;
        List<LocalDate> datesCounted = new ArrayList<>();
        for (Turnover t : toCY){
            purchaseCountAggregateForPeriod.setCount(aggCount(purchaseCountAggregateForPeriod.getCount(), t.getPurchaseCount()));
            if (t.getPurchaseCount()!=null && t.getPurchaseCount().intValue() > 0){
                if (!datesCounted.contains(t.getDate())) {
                    datesCounted.add(t.getDate());
                    numberOfCountsCY = numberOfCountsCY == null ? 1 : numberOfCountsCY + 1;
                }
            }
        }
        Integer numberOfCountsPY = null;
        datesCounted.clear();
        for (Turnover t : toPY ){
            purchaseCountAggregateForPeriod.setCountPreviousYear(aggCount(purchaseCountAggregateForPeriod.getCountPreviousYear(), t.getPurchaseCount()));
            if (t.getPurchaseCount()!=null  && t.getPurchaseCount().intValue() > 0){
                if (!datesCounted.contains(t.getDate())) {
                    datesCounted.add(t.getDate());
                    numberOfCountsPY = numberOfCountsPY == null ? 1 : numberOfCountsPY + 1;
                }
            }
        }

        purchaseCountAggregateForPeriod.setComparable(
                isComparableForPeriod(purchaseCountAggregateForPeriod.getAggregationPeriod()
                , numberOfCountsCY
                , numberOfCountsPY
                , false
                , false
        ));

    }

    List<Turnover> getTurnoversForPurchaseCountAggregateForPeriod(
            final PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod,
            final TurnoverAggregation aggregation,
            final boolean previousYear) {
        return getTurnoversForPurchaseCountAggregateForPeriod(purchaseCountAggregateForPeriod, aggregation.getDate(), aggregation.getTurnovers().stream().collect(
                Collectors.toList()), previousYear);
    }

    List<Turnover> getTurnoversForPurchaseCountAggregateForPeriod(
            final PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod,
            final LocalDate aggregationDate,
            final List<Turnover> turnovers,
            final boolean previousYear){
        return getTurnoversForAggregationPeriod(purchaseCountAggregateForPeriod.getAggregationPeriod(), aggregationDate, turnovers, previousYear);
    }

    List<Turnover> getTurnoversForAggregationPeriod(
            final AggregationPeriod aggregationPeriod,
            final LocalDate aggregationDate,
            final List<Turnover> turnovers,
            final boolean previousYear){
        final LocalDate periodStartDate = aggregationPeriod.periodStartDateFor(aggregationDate);
        final LocalDate periodEndDate = aggregationDate;
        if (periodStartDate.isAfter(periodEndDate)) return Arrays.asList();
        final LocalDateInterval interval = LocalDateInterval.including(previousYear ? periodStartDate.minusYears(1) : periodStartDate, previousYear ? periodEndDate.minusYears(1) : periodEndDate);
        // ECP-1211: in case AggregationPeriod P_12M_COVID take out turnovers during covid period
        if (aggregationPeriod == AggregationPeriod.P_12M_COVID){
            LocalDateInterval covidInterval = LocalDateInterval.including(new LocalDate(2020,3,1), new LocalDate(2020,5,31));
            return turnovers.stream()
                    .filter(t->interval.contains(t.getDate()))
                    .filter(t->!covidInterval.contains(t.getDate()))
                    .collect(Collectors.toList());
        }
        return turnovers.stream().filter(t->interval.contains(t.getDate())).collect(Collectors.toList());
    }

    void maintainTurnoverAggregationsForConfig(final AggregationAnalysisReportForConfig report){

        try {
            final TurnoverReportingConfig config = report.getTurnoverReportingConfig();
            final List<TurnoverAggregation> aggregations = turnoverAggregationRepository
                    .findByTurnoverReportingConfig(config);
            final List<LocalDate> currentAggDates = aggregations.stream().map(a -> a.getDate())
                    .collect(Collectors.toList());

            // create if needed
            report.getAggregationDates().stream().forEach(d -> {
                if (!currentAggDates.contains(d)) {
                    turnoverAggregationRepository.findOrCreate(config, d, config.getCurrency());
                }
            });
            // remove if needed
            currentAggDates.stream().forEach(d -> {
                if (!report.getAggregationDates().contains(d)) {
                    turnoverAggregationRepository.findUnique(config, d).remove();
                }
            });
        } catch (Exception e){
            LOG.warn(String.format("Problem with aggregation for lease %s and occ %s",
                    report.getTurnoverReportingConfig().getOccupancy().getLease().getReference(),
                    report.getTurnoverReportingConfig().getOccupancy()));
            LOG.warn(e.getMessage());
        }

    }


    Boolean isComparableToDate(final LocalDate aggregationDate, final Integer numberOfTurnoversThisYear, final Integer numberOfTurnoversPreviousYear, final Boolean nonComparableThisYear, final Boolean nonComparablePreviousYear ){
         if (numberOfTurnoversThisYear==null || numberOfTurnoversPreviousYear == null) return false;
         return !nonComparableThisYear && !nonComparablePreviousYear && numberOfTurnoversThisYear >= getMinNumberOfTurnoversToDate(aggregationDate) && numberOfTurnoversPreviousYear >= getMinNumberOfTurnoversToDate(aggregationDate);
    }

    Boolean containsNonComparableTurnover(final List<Turnover> turnoverList){
        if (turnoverList.isEmpty()) return null;
        return turnoverList.stream().anyMatch(t->t.isNonComparable());
    }

    Boolean isComparableForPeriod(final AggregationPeriod period, final Integer numberOfTurnoversThisYear, final Integer numberOfTurnoversPreviousYear, final Boolean nonComparableThisYear, final Boolean nonComparablePreviousYear){
        if (numberOfTurnoversThisYear==null || numberOfTurnoversPreviousYear==null) return false;
        return !nonComparableThisYear && !nonComparablePreviousYear && numberOfTurnoversThisYear >= period.getMinNumberOfTurnovers() && numberOfTurnoversPreviousYear >=period.getMinNumberOfTurnovers();
    }

    private BigDecimal aggAmount(final BigDecimal curAmount, final BigDecimal amountToAdd) {
        if (amountToAdd==null && curAmount==null) return null;
        if (amountToAdd==null) return curAmount;
        if (curAmount==null)   return amountToAdd;
        return curAmount.add(amountToAdd);
    }

    private BigInteger aggCount(final BigInteger curCount, final BigInteger countToAdd) {
        if (countToAdd==null && curCount==null) return null;
        if (countToAdd==null) return curCount;
        if (curCount==null)   return countToAdd;
        return curCount.add(countToAdd);
    }

    int getMinNumberOfTurnoversToDate(final LocalDate aggregationDate){
        return aggregationDate.getMonthOfYear();
    }

    private void resetTurnoverAggregateToDate(final TurnoverAggregateToDate agg) {
        agg.setGrossAmount(null);
        agg.setNetAmount(null);
        agg.setTurnoverCount(null);
        agg.setNonComparableThisYear(null);
        agg.setGrossAmountPreviousYear(null);
        agg.setNetAmountPreviousYear(null);
        agg.setTurnoverCountPreviousYear(null);
        agg.setNonComparablePreviousYear(null);
        agg.setComparable(false);
    }

    private void resetTurnoverAggregateForPeriod(final TurnoverAggregateForPeriod agg){
        agg.setGrossAmount(null);
        agg.setNetAmount(null);
        agg.setTurnoverCount(null);
        agg.setNonComparableThisYear(null);
        agg.setGrossAmountPreviousYear(null);
        agg.setNetAmountPreviousYear(null);
        agg.setTurnoverCountPreviousYear(null);
        agg.setNonComparablePreviousYear(null);
        agg.setComparable(false);
    }

    private void resetPurchaseCountAggregateForPeriod(final PurchaseCountAggregateForPeriod agg){
        agg.setCount(null);
        agg.setCountPreviousYear(null);
        agg.setComparable(false);
    }

    public List<TurnoverReportingConfig> choicesForChildConfig(final TurnoverReportingConfig config) {
        Lease parentLease = config.getOccupancy().getLease();
        List<TurnoverReportingConfig> result = new ArrayList<>();
        if (parentLease.getPrevious()!=null) {
            Lease childLease = (Lease) parentLease.getPrevious();
            Lists.newArrayList(childLease.getOccupancies()).stream()
                    .forEach(o->{
                        result.addAll(turnoverReportingConfigRepository.findByOccupancyAndTypeAndFrequency(o, Type.PRELIMINARY, Frequency.MONTHLY));
                    });
        }
        // ECP-1205: also add configs of occs on the same lease that are terminated before the config starts
        final List<Occupancy> closedOccupanciesOnSameLeaseWithEndDateBeforeStartDateConfig = Lists.newArrayList(parentLease.getOccupancies()).stream()
                .filter(o -> o.getEndDate() != null && o.getEndDate().isBefore(config.getStartDate()))
                .collect(Collectors.toList());
        closedOccupanciesOnSameLeaseWithEndDateBeforeStartDateConfig.forEach(o->{
            result.addAll(turnoverReportingConfigRepository.findByOccupancyAndTypeAndFrequency(o,Type.PRELIMINARY, Frequency.MONTHLY));
        });
        return result;
    }


    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject TurnoverAggregateForPeriodRepository turnoverAggregateForPeriodRepository;

    @Inject ClockService clockService;

    @Inject TurnoverAnalysisService turnoverAnalysisService;
}
