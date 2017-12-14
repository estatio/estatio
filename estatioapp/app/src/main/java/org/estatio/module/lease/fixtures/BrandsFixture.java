package org.estatio.module.lease.fixtures;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.fixtures.brands.enums.Brand_enum;

public class BrandsFixture extends FixtureScript {

//    public static final String YU_GROUP = "Yu Group";
////    public static final String YU_S_CLEANING_SERVICES = "Yu's Cleaning Services";
////    public static final String HAPPY_VALLEY = "Happy Valley";

//    @Inject
//    BrandRepository brandRepository;
//
//    @Inject
//    CountryRepository countryRepository;
//
//    @Inject
//    ApplicationTenancyRepository applicationTenancyRepository;

    @Override protected void execute(final ExecutionContext ec) {

        ec.executeChildren(this,
                Brand_enum.Yu_s_Noodle_Joint,
                Brand_enum.Yu_s_Cleaning_Services,
                Brand_enum.Happy_ValLey);

//        brandRepository.newBrand(YU_S_NOODLE_JOINT, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(
//                Country_enum.NLD.getRef3()), YU_GROUP, applicationTenancyRepository.findByPath("/"));
//        brandRepository.newBrand(YU_S_CLEANING_SERVICES, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(Country_enum.NLD.getRef3()), YU_GROUP, applicationTenancyRepository.findByPath("/"));
//        brandRepository.newBrand(HAPPY_VALLEY, BrandCoverage.INTERNATIONAL, countryRepository.findCountry(Country_enum.NLD.getRef3()), null, applicationTenancyRepository.findByPath("/"));

    }
}
