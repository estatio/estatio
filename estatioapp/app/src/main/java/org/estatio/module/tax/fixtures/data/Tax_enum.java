package org.estatio.module.tax.fixtures.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;
import org.estatio.module.tax.fixtures.builders.TaxBuilder;

import static java.util.Arrays.asList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Tax_enum implements PersonaWithBuilderScript<Tax, TaxBuilder>, PersonaWithFinder<Tax> {

    GB_VATSTD(Country_enum.GBR, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    NL_VATSTD(Country_enum.NLD, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    FR_VATSTD(Country_enum.FRA, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    SW_VATSTD(Country_enum.SWE, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21)))),
    IT_VATSTD(Country_enum.ITA, "VATSTD", asList(rate(ld(1980, 1, 1), bd(19)), rate(ld(2011, 9, 17), bd(21))));

    private final Country_enum country_d;
    private final String referenceSuffix;
    private final List<TaxBuilder.RateData> rates;

    private static TaxBuilder.RateData rate(final LocalDate ld, final BigDecimal bd) {
        return new TaxBuilder.RateData(ld, bd);
    }

    public String getReference() {
        return country_d.getRef3() + "-" + referenceSuffix;
    }


    @Override
    public Tax findUsing(final ServiceRegistry2 serviceRegistry) {
        final TaxRepository taxRepository = serviceRegistry.lookupService(TaxRepository.class);
        return taxRepository.findByReference(getReference());
    }


    @Override
    public TaxBuilder builder() {

        return new TaxBuilder()
                .setPrereq((f,ec) -> f.setCountry(f.objectFor(country_d, ec)))
                .setPrereq((f,ec) -> f.setApplicationTenancy(f.objectFor(country_d.getApplicationTenancy_d(), ec)))
                .setRef(Tax_enum.this.getReference())
                .setRates(rates);

    }

}
