package org.estatio.module.turnover.contributions;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;

@Mixin
public class Occupancy_createTurnoverReportingConfig {

    private final Occupancy occupancy;

    public Occupancy_createTurnoverReportingConfig(final Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Occupancy createTurnoverReportingConfig(final Type type, final LocalDate startDate, final Frequency frequency, final Currency currency) {
        turnoverReportingConfigRepository.findOrCreate(occupancy, type, null, startDate, frequency, currency);
        return occupancy;
    }

    public Type default0CreateTurnoverReportingConfig(){
        return Type.PRELIMINARY;
    }

    public LocalDate default1CreateTurnoverReportingConfig(){
        return occupancy.getStartDate();
    }

    public Frequency default2CreateTurnoverReportingConfig(){
        return Frequency.MONTHLY;
    }

    public Currency default3CreateTurnoverReportingConfig(){
        return occupancy.getAtPath().startsWith("/SWE") ? currencyRepository.findCurrency(Currency_enum.SEK.getReference()) : currencyRepository.findCurrency(Currency_enum.EUR.getReference());
    }

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject
    CurrencyRepository currencyRepository;

}
