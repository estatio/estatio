package org.estatio.module.turnover.imports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.user.UserService;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.base.dom.Importable;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.turnover.imports.TurnoverImport"
)
public class TurnoverImport implements Importable, ExcelFixtureRowHandler, FixtureAwareRowHandler<TurnoverImport> {

    private static final Logger LOG = LoggerFactory.getLogger(TurnoverImport.class);

    /* needed for excel import */
    public TurnoverImport(){}

    public TurnoverImport(
            final String leaseReference,
            final String unitReference,
            final LocalDate date,
            final BigDecimal grossAmount,
            final BigDecimal netAmount,
            final String type,
            final String frequency,
            final String currencyReference,
            final int nonComparableFlag,
            final BigInteger purchaseCount,
            final String comments) {
        this.leaseReference = leaseReference;
        this.unitReference = unitReference;
        this.date = date;
        this.grossAmount = grossAmount;
        this.netAmount = netAmount;
        this.type = type;
        this.frequency = frequency;
        this.currencyReference = currencyReference;
        this.nonComparableFlag = nonComparableFlag;
        this.purchaseCount = purchaseCount;
        this.comments = comments;
    }

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String unitReference;

    @Getter @Setter
    private LocalDate date;

    @Getter @Setter
    private BigDecimal grossAmount;

    @Getter @Setter
    private BigDecimal netAmount;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private String frequency;

    @Getter @Setter
    private String currencyReference;

    @Getter @Setter
    private int nonComparableFlag;

    @Getter @Setter
    private BigInteger purchaseCount;

    @Getter @Setter
    private String comments;


    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        Type typeEnum;

        try {
            typeEnum = Type.valueOf(type);
        } catch (Exception e){
            logAndWarn(String.format("Type not found for %s", type));
            return Lists.newArrayList();
        }

        Frequency frequencyEnum;

        try {
            frequencyEnum = Frequency.valueOf(frequency);
        } catch (Exception e){
            logAndWarn(String.format("Frequency not found for %s", frequency));
            return Lists.newArrayList();
        }

        final Lease lease = leaseRepository.findLeaseByReference(leaseReference);
        if (lease==null) {
            logAndWarn(String.format("Lease not found for reference %s", leaseReference));
            return Lists.newArrayList();
        }

        final Unit unit = unitRepository.findUnitByReference(unitReference);
        if (unit==null) {
            logAndWarn(String.format("Unit not found for reference %s", unitReference));
            return Lists.newArrayList();
        }

        final Currency currency = currencyRepository.findCurrency(currencyReference);
        if (currency==null) {
            logAndWarn(String.format("Currency not found for reference %s", currencyReference));
            return Lists.newArrayList();
        }

        final List<Occupancy> activeOccupanciesForLeaseOnDateAndUnit = occupancyRepository.findByLeaseAndDate(lease, date)
                .stream()
                .filter(o->o.getUnit().equals(unit))
                .filter(o->o.getEndDate()==null || !o.getEndDate().isBefore(date))
                .collect(Collectors.toList());

        if (activeOccupanciesForLeaseOnDateAndUnit.isEmpty()){
            logAndWarn(String.format("No active occupancy found for lease %s and unit %s on date %s", leaseReference, unitReference, date));
            return Lists.newArrayList();
        }

        if (activeOccupanciesForLeaseOnDateAndUnit.size()>1){
            logAndWarn(String.format("Multiple active occupancies found for lease %s and unit %s on date %s", leaseReference, unitReference, date));
            return Lists.newArrayList();
        }

        Occupancy occupancy = activeOccupanciesForLeaseOnDateAndUnit.get(0);
        Turnover turnover = turnoverRepository.create(
                occupancy,
                date,
                typeEnum,
                frequencyEnum,
                LocalDateTime.now(),
                userService.getUser().getName(),
                currency,
                netAmount==null || netAmount.equals(BigDecimal.ZERO) ? null : netAmount,
                grossAmount==null || grossAmount.equals(BigDecimal.ZERO) ? null : grossAmount,
                purchaseCount==null || purchaseCount.equals(BigInteger.ZERO) ? null : purchaseCount,
                comments,
                nonComparableFlag > 0 ? true: false);

        return Lists.newArrayList(turnover);
    }

    private void logAndWarn(final String message) {
        LOG.warn(message);
        messageService.warnUser(message);
    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);
    }

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    @Override
    public void handleRow(final TurnoverImport previousRow) {

        if (executionContext != null && excelFixture2 != null) {
            if (executionContext.getParameterAsBoolean("testMode") != null && executionContext.getParameterAsBoolean("testMode")) {
                executionContext.addResult(excelFixture2, this.importData(previousRow));
            } else {
                this.importData(previousRow);
            }
        }

    }

    @Inject OccupancyRepository occupancyRepository;

    @Inject LeaseRepository leaseRepository;

    @Inject UnitRepository unitRepository;

    @Inject CurrencyRepository currencyRepository;

    @Inject MessageService messageService;

    @Inject TurnoverRepository turnoverRepository;

    @Inject UserService userService;

}
