package org.estatio.module.turnover.imports;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.message.MessageService;

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
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.turnover.imports.TurnoverReportingConfigImport"
)
public class TurnoverReportingConfigImport implements Importable, ExcelFixtureRowHandler, FixtureAwareRowHandler<TurnoverReportingConfigImport> {

    private static final Logger LOG = LoggerFactory.getLogger(TurnoverReportingConfigImport.class);

    /* needed for excel import */
    public TurnoverReportingConfigImport(){}

    public TurnoverReportingConfigImport(
            final String leaseReference,
            final String unitReference,
            final LocalDate occupancyStartDate,
            final String currencyReference) {
        this.leaseReference = leaseReference;
        this.unitReference = unitReference;
        this.occupancyStartDate = occupancyStartDate;
        this.currencyReference = currencyReference;
    }

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String unitReference;

    @Getter @Setter
    private LocalDate occupancyStartDate;

    @Getter @Setter
    private String currencyReference;

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

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

        final Occupancy occupancy = occupancyRepository.findByLeaseAndUnitAndStartDate(lease, unit, occupancyStartDate);

        if (occupancy==null){
            logAndWarn(String.format("No occupancy found for lease %s and unit %s on date %s", leaseReference, unitReference, occupancyStartDate));
            return Lists.newArrayList();
        }

        TurnoverReportingConfig config = turnoverReportingConfigRepository.upsert(occupancy, Type.PRELIMINARY, null, occupancyStartDate, Frequency.MONTHLY, currency);

        return Lists.newArrayList(config);
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
    public void handleRow(final TurnoverReportingConfigImport previousRow) {

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

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

}
