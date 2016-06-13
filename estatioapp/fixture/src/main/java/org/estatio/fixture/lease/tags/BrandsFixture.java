package org.estatio.fixture.lease.tags;

import javax.inject.Inject;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.geography.CountryRepository;
import org.estatio.dom.lease.tags.BrandCoverage;
import org.estatio.dom.lease.tags.Brands;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.geography.CountriesRefData;

public class BrandsFixture extends EstatioFixtureScript {

    public static final String YU_S_NOODLE_JOINT = "Yu's Noodle Joint";
    public static final String YU_GROUP = "Yu Group";
    public static final String YU_S_CLEANING_SERVICES = "Yu's Cleaning Services";
    public static final String HAPPY_VALLEY = "Happy Valley";

    @Inject
    Brands brands;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Override protected void execute(final ExecutionContext executionContext) {

        if(isExecutePrereqs()) {
            executionContext.executeChild(this, new EstatioBaseLineFixture());
        }

        brands.newBrand(YU_S_NOODLE_JOINT, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(CountriesRefData.NLD), YU_GROUP, applicationTenancyRepository.findByPath("/"));
        brands.newBrand(YU_S_CLEANING_SERVICES, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(CountriesRefData.NLD), YU_GROUP, applicationTenancyRepository.findByPath("/"));
        brands.newBrand(HAPPY_VALLEY, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(CountriesRefData.NLD), null, applicationTenancyRepository.findByPath("/"));

    }
}
