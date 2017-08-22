package org.estatio.tax.fixture.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.dom.fixture.DemoData2;
import org.estatio.dom.fixture.DemoData2PersistAbstract;
import org.estatio.tax.dom.Tax;

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
public enum Tax_data implements DemoData2<Tax_data, Tax> {

    GB_VATSTD(Country_data.GBR, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    NL_VATSTD(Country_data.NLD, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    FR_VATSTD(Country_data.FRA, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    SW_VATSTD(Country_data.SWE, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    IT_VATSTD(Country_data.ITA, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21))));

    private final Country_data countryData;
    private final String referenceSuffix;
    private final List<RateData> rates;

    private static RateData rate(final LocalDate ld, final BigDecimal bd) {
        return new RateData(ld, bd);
    }

    public String getReference() {
        return countryData.getRef2() + "-" + referenceSuffix;
    }

    @Data
    static class RateData {
        private final LocalDate date;
        private final BigDecimal rateValue;
    }

    @Override
    public Tax asDomainObject(final ServiceRegistry2 serviceRegistry2) {
        final Tax tax = Tax.builder()
                .applicationTenancyPath(countryData.getAtPath())
                .reference(getReference())
                .name("Value Added Tax (Standard, " + countryData.getRef2() + ")")
                .build();
        serviceRegistry2.injectServicesInto(tax);
        for (RateData rate : rates) {
            tax.newRate(rate.date, rate.rateValue);
        }
        return tax;
    }

    public static class PersistScript extends DemoData2PersistAbstract<PersistScript, Tax_data, Tax> {
        public PersistScript() {
            super(Tax_data.class);
        }
    }

}
