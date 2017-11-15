package org.estatio.module.country.fixtures.enums;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.base.platform.fixturesupport.DemoData2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Country_enum implements DemoData2<Country_enum, Country> {

    GBR("GBR", "GB", "United Kingdom",   ApplicationTenancy_enum.Gb),
    NLD("NLD", "NL", "The Netherlands", ApplicationTenancy_enum.Nl),
    ITA("ITA", "IT", "Italy",           ApplicationTenancy_enum.It),
    FRA("FRA", "FR", "France",          ApplicationTenancy_enum.Fr),
    SWE("SWE", "SE", "Sweden",          ApplicationTenancy_enum.Se);

    private final String ref3;
    private final String ref2;
    private final String name;
    private final ApplicationTenancy_enum appTenancyData;

    @Override
    public Country asDomainObject(final ServiceRegistry2 serviceRegistry2) {
        return new Country(this.ref3, this.ref2, this.name);
    }

    public String getAtPath(){
        return appTenancyData.getPath();
    }

    @Override
    public Country findUsing(final ServiceRegistry2 serviceRegistry) {
        return findByPath(serviceRegistry, this.ref3);
    }

    private static Country findByPath(final ServiceRegistry2 serviceRegistry2, final String reference) {
        return serviceRegistry2.lookupService(CountryRepository.class).findCountry(reference);
    }

}
