package org.estatio.module.lease.fixtures.brands.builder;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.dom.occupancy.tags.BrandRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"name"}, callSuper = false)
@ToString(of={"name"})
@Accessors(chain = true)
public class BrandBuilder extends BuilderScriptAbstract<Brand, BrandBuilder> {

    @Getter @Setter
    String name;

    @Getter @Setter //defaulted
    BrandCoverage coverage;
    @Getter @Setter // optional
    String group;

    @Getter @Setter // optional
    BrandCoverage brandCoverage;
    @Getter @Setter // optional
    Country countryOfOrigin;

    @Getter @Setter // defaulted
    ApplicationTenancy applicationTenancy;


        @Getter
    Brand object;

    @Override protected void execute(final ExecutionContext executionContext) {

        checkParam("name", executionContext, String.class);
        defaultParam("brandCoverage", executionContext, BrandCoverage.INTERNATIONAL);
        defaultParam("atPath", executionContext, "/");

        final Brand brand = brandRepository.newBrand(name, brandCoverage, countryOfOrigin, group, applicationTenancy);

        executionContext.addResult(this, brand);

        object = brand;
    }

    @Inject
    BrandRepository brandRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;


}
