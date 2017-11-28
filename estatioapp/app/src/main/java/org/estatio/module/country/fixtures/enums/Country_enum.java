package org.estatio.module.country.fixtures.enums;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.base.platform.fixturesupport.DataEnum2;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Country_enum implements DataEnum2<Country, FixtureScript> {

    GBR("GBR", "GB", "United Kingdom",   ApplicationTenancy_enum.Gb),
    NLD("NLD", "NL", "The Netherlands", ApplicationTenancy_enum.Nl),
    ITA("ITA", "IT", "Italy",           ApplicationTenancy_enum.It),
    FRA("FRA", "FR", "France",          ApplicationTenancy_enum.Fr),
    SWE("SWE", "SE", "Sweden",          ApplicationTenancy_enum.Se);

    private final String ref3;
    private final String ref2;
    private final String name;
    private final ApplicationTenancy_enum applicationTenancy;

    public String getAtPath(){
        return applicationTenancy.getPath();
    }

    @Override
    public Country upsertUsing(final ServiceRegistry2 serviceRegistry) {
        final CountryRepository countryRepository =
                serviceRegistry.lookupService(CountryRepository.class);
        return countryRepository.createCountry(this.ref3, this.ref2, this.name);
    }

    @Override
    public Country findUsing(final ServiceRegistry2 serviceRegistry) {
        final CountryRepository countryRepository =
                serviceRegistry.lookupService(CountryRepository.class);
        return countryRepository.findCountry(this.ref3);
    }

    @Override
    public FixtureScript toFixtureScript() {
        return new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                final Country country = upsertUsing(serviceRegistry);
                executionContext.addResult(this, country);
            }
        };
    }

}
