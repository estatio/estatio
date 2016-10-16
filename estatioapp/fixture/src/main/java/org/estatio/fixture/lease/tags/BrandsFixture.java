package org.estatio.fixture.lease.tags;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.CountryRepository;
import org.estatio.dom.lease.tags.BrandCoverage;
import org.estatio.dom.lease.tags.BrandRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.incode.module.country.fixture.CountriesRefData;

public class BrandsFixture extends FixtureScript {

    public static final String YU_S_NOODLE_JOINT = "Yu's Noodle Joint";
    public static final String YU_GROUP = "Yu Group";
    public static final String YU_S_CLEANING_SERVICES = "Yu's Cleaning Services";
    public static final String HAPPY_VALLEY = "Happy Valley";

    @Inject
    BrandRepository brandRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Override protected void execute(final ExecutionContext executionContext) {

        executionContext.executeChild(this, new EstatioBaseLineFixture());

        brandRepository.newBrand(YU_S_NOODLE_JOINT, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(CountriesRefData.NLD), YU_GROUP, applicationTenancyRepository.findByPath("/"));
        brandRepository.newBrand(YU_S_CLEANING_SERVICES, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(CountriesRefData.NLD), YU_GROUP, applicationTenancyRepository.findByPath("/"));
        brandRepository.newBrand(HAPPY_VALLEY, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(CountriesRefData.NLD), null, applicationTenancyRepository.findByPath("/"));

    }
}
