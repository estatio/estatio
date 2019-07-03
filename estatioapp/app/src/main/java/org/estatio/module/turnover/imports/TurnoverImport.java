package org.estatio.module.turnover.imports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.commons.lang.ArrayExtensions;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;
import org.isisaddons.module.security.app.user.MeService;

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
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
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
            final String leaseName,
            final String unitReference,
            final LocalDate occupancyStartDate,
            final LocalDate date,
            final BigDecimal grossAmount,
            final BigDecimal netAmount,
            final BigDecimal vatPercentage,
            final String type,
            final String frequency,
            final String currencyReference,
            final int nonComparableFlag,
            final BigInteger purchaseCount,
            final String comments,
            final String reportedBy,
            final LocalDateTime reportedAt,
            final BigDecimal grossAmountPreviousYear,
            final BigDecimal netAmountPreviousYear,
            final BigInteger purchaseCountPreviousYear) {
        this.leaseReference = leaseReference;
        this.leaseName = leaseName;
        this.unitReference = unitReference;
        this.occupancyStartDate = occupancyStartDate;
        this.date = date;
        this.grossAmount = grossAmount;
        this.netAmount = netAmount;
        this.vatPercentage = vatPercentage;
        this.type = type;
        this.frequency = frequency;
        this.currency = currencyReference;
        this.nonComparableFlag = nonComparableFlag;
        this.purchaseCount = purchaseCount;
        this.comments = comments;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
        this.grossAmountPreviousYear = grossAmountPreviousYear;
        this.netAmountPreviousYear = netAmountPreviousYear;
        this.purchaseCountPreviousYear = purchaseCountPreviousYear;
    }

    public TurnoverImport(final Turnover turnover, final Turnover turnoverPreviousYear) {
        this.leaseReference = turnover.getConfig().getOccupancy().getLease().getReference();
        this.leaseName = turnover.getConfig().getOccupancy().getLease().getName();
        this.unitReference = turnover.getConfig().getOccupancy().getUnit().getReference();
        this.occupancyStartDate = turnover.getConfig().getOccupancy().getStartDate();
        this.date = turnover.getDate();
        this.grossAmount = turnover.getGrossAmount();
        this.netAmount = turnover.getNetAmount();
        this.type = turnover.getType().name();
        this.frequency = turnover.getFrequency().name();
        this.currency = turnover.getCurrency().getReference();
        this.nonComparableFlag = turnover.isNonComparable() ? 1 : 0;
        this.purchaseCount = turnover.getPurchaseCount();
        this.comments = turnover.getComments();
        this.grossAmountPreviousYear = turnoverPreviousYear!=null ? turnoverPreviousYear.getGrossAmount() : null;
        this.netAmountPreviousYear = turnoverPreviousYear!=null ? turnoverPreviousYear.getNetAmount() : null;
        this.purchaseCountPreviousYear = turnoverPreviousYear!=null ? turnoverPreviousYear.getPurchaseCount() : null;
    }

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String leaseReference;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String leaseName;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String unitReference;

    @Getter @Setter
    @MemberOrder(sequence = "17")
    private LocalDate occupancyStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private LocalDate date;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal grossAmount;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    private BigDecimal netAmount;

    @Getter @Setter
    @MemberOrder(sequence = "15")
    private BigDecimal vatPercentage;

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String type;

    @Getter @Setter
    @MemberOrder(sequence = "14")
    private String frequency;

    @Getter @Setter
    @MemberOrder(sequence = "16")
    private String currency;

    @Getter @Setter
    @MemberOrder(sequence = "12")
    private int nonComparableFlag;

    @Getter @Setter
    @MemberOrder(sequence = "8")
    private BigInteger purchaseCount;

    @Getter @Setter
    @MemberOrder(sequence = "13")
    private String comments;

    @Getter @Setter
    @MemberOrder(sequence = "18")
    private LocalDateTime reportedAt;

    @Getter @Setter
    @MemberOrder(sequence = "19")
    private String reportedBy;

    @Getter @Setter
    @MemberOrder(sequence = "9")
    private BigDecimal grossAmountPreviousYear;

    @Getter @Setter
    @MemberOrder(sequence = "10")
    private BigDecimal netAmountPreviousYear;

    @Getter @Setter
    @MemberOrder(sequence = "11")
    private BigInteger purchaseCountPreviousYear;

    public BigDecimal getNetAmountDivPercentage(){
        if (getNetAmountPreviousYear() == null || getNetAmountPreviousYear().compareTo(BigDecimal.ZERO) == 0) return new BigDecimal("100");
        BigDecimal numerator = getNetAmount()!=null ? getNetAmount().subtract(getNetAmountPreviousYear()) : BigDecimal.ZERO.subtract(getNetAmountPreviousYear());
        return numerator.divide(getNetAmountPreviousYear(), MathContext.DECIMAL64).multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getGrossAmountDivPercentage(){
        if (getGrossAmountPreviousYear() == null || getGrossAmountPreviousYear().compareTo(BigDecimal.ZERO) == 0) return new BigDecimal("100");
        BigDecimal numerator = getGrossAmount()!=null ? getGrossAmount().subtract(getGrossAmountPreviousYear()) : BigDecimal.ZERO.subtract(getGrossAmountPreviousYear());
        return numerator.divide(getGrossAmountPreviousYear(), MathContext.DECIMAL64).multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

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

        final Currency currency = currencyRepository.findCurrency(this.currency);
        if (currency==null) {
            logAndWarn(String.format("Currency not found for reference %s", this.currency));
            return Lists.newArrayList();
        }

        final Occupancy occupancy = occupancyRepository.findByLeaseAndUnitAndStartDate(lease, unit, occupancyStartDate);

        if (occupancy==null){
            logAndWarn(String.format("No config found for lease %s and unit %s on date %s", leaseReference, unitReference, occupancyStartDate));
            return Lists.newArrayList();
        }

        final TurnoverReportingConfig config = turnoverReportingConfigRepository.findUnique(occupancy, typeEnum);

        if (config==null){
            logAndWarn(String.format("No reporting config found for lease %s and unit %s on date %s for type %s", leaseReference, unitReference, occupancyStartDate, typeEnum.name()));
            return Lists.newArrayList();
        }

        final LocalDateTime reportedAtToUse = ArrayExtensions.coalesce(reportedAt, LocalDateTime.now());

        final String reportedByToUse = ArrayExtensions.coalesce(reportedBy, meService.me().getUsername());

        Turnover turnover = turnoverRepository.upsert(
                config,
                date,
                typeEnum,
                frequencyEnum,
                Status.APPROVED,
                reportedAtToUse,
                reportedByToUse,
                currency,
                netAmountToUse(),
                grossAmount,
                purchaseCount==null || purchaseCount.equals(BigInteger.ZERO) ? null : purchaseCount, // just because when using TurnoverImportXlsxFixture, somehow the values are set to 0 instead of null like in production
                comments,
                nonComparableFlag > 0 ? true: false);

        return Lists.newArrayList(turnover);
    }

    BigDecimal netAmountToUse(){
        if (netAmount!=null) return netAmount;
        if (grossAmount!=null && vatPercentage!=null) return
                grossAmount.divide(
                    BigDecimal.ONE.add(
                            vatPercentage.divide(new BigDecimal("100"), MathContext.DECIMAL64)
                    )
                ).setScale(2, BigDecimal.ROUND_HALF_UP);
        return null;
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

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject MeService meService;

}
