package org.estatio.module.tax.fixtures.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.base.platform.fixturesupport.EnumWithFixtureScript;
import org.isisaddons.module.base.platform.fixturesupport.EnumWithUpsert;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRate;
import org.estatio.module.tax.dom.TaxRepository;

import static java.util.Arrays.asList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Tax_enum implements EnumWithUpsert<Tax>, EnumWithFixtureScript<Tax, FixtureScript> {

    GB_VATSTD(Country_enum.GBR, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    NL_VATSTD(Country_enum.NLD, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    FR_VATSTD(Country_enum.FRA, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    SW_VATSTD(Country_enum.SWE, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    IT_VATSTD(Country_enum.ITA, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21))));

    private final Country_enum country;
    private final String referenceSuffix;
    private final List<RateData> rates;

    private static RateData rate(final LocalDate ld, final BigDecimal bd) {
        return new RateData(ld, bd);
    }

    public String getReference() {
        return country.getRef3() + "-" + referenceSuffix;
    }

    @Data
    static class RateData {
        private final LocalDate date;
        private final BigDecimal rateValue;
    }


    @Override
    public Tax findUsing(final ServiceRegistry2 serviceRegistry) {
        final TaxRepository taxRepository = serviceRegistry.lookupService(TaxRepository.class);
        return taxRepository.findByReference(getReference());
    }

    @Override
    public Tax upsertUsing(final ServiceRegistry2 serviceRegistry) {
        Tax tax = findUsing(serviceRegistry);
        if(tax != null) {
            return tax;
        }
        final TaxRepository taxRepository = serviceRegistry.lookupService(TaxRepository.class);

        final String name = "Value Added Tax (Standard, " + country.getRef3() + ")";
        final ApplicationTenancy applicationTenancy = country.getApplicationTenancy().findUsing(serviceRegistry);
        tax = taxRepository.newTax(getReference(), name, applicationTenancy);
        for (RateData rate : rates) {
            final TaxRate taxRate = tax.newRate(rate.date, rate.rateValue);
            tax.getRates().add(taxRate);
        }
        return tax;
    }

    @Override
    public FixtureScript toFixtureScript() {
        return new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.addResult(this, upsertUsing(serviceRegistry));
            }
        };
    }

}
