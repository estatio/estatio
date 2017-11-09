package org.estatio.module.base.fixtures.country.data;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.fixtures.security.apptenancy.data.ApplicationTenancy_data;
import org.estatio.module.base.platform.fixturesupport.DemoData2;
import org.estatio.module.base.platform.fixturesupport.DemoData2PersistAbstract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Country_data implements DemoData2<Country_data, Country> {

    GBR("GBR", "GB", "Great Britain",   ApplicationTenancy_data.Gb),
    NLD("NLD", "NL", "The Netherlands", ApplicationTenancy_data.Nl),
    ITA("ITA", "IT", "Italy",           ApplicationTenancy_data.It),
    FRA("FRA", "FR", "France",          ApplicationTenancy_data.Fr),
    SWE("SWE", "SE", "Sweden",          ApplicationTenancy_data.Se);

    private final String ref3;
    private final String ref2;
    private final String name;
    private final ApplicationTenancy_data appTenancyData;

    @Override
    public Country asDomainObject(final ServiceRegistry2 serviceRegistry2) {
        return new Country(this.ref3, this.ref2, this.name);
    }

    public String getAtPath(){
        return appTenancyData.getPath();
    }

    public static class PersistScript extends DemoData2PersistAbstract<PersistScript, Country_data, Country> {
        public PersistScript() {
            super(Country_data.class);
        }
    }

}
