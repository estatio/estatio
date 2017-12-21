package org.estatio.module.lease.fixtures.brands.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.incode.module.country.fixtures.enums.Country_enum;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.dom.occupancy.tags.BrandRepository;
import org.estatio.module.lease.fixtures.brands.builder.BrandBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Global;
import static org.incode.module.country.fixtures.enums.Country_enum.NLD;
import static org.estatio.module.lease.dom.occupancy.tags.BrandCoverage.INTERNATIONAL;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Brand_enum
        implements PersonaWithFinder<Brand>, PersonaWithBuilderScript<Brand, BrandBuilder> {


    Yu_s_Noodle_Joint ("Yu's Noodle Joint", INTERNATIONAL, "Yu Group", NLD, Global),
    Yu_s_Cleaning_Services ("Yu's Cleaning Services", INTERNATIONAL, "Yu Group", NLD, Global),
    Happy_ValLey ("Happy Valley", INTERNATIONAL, "Yu Group", NLD, Global)
    ;

    private final String name;
    private final BrandCoverage coverage;
    private final String group;
    private final Country_enum country_d;
    private final ApplicationTenancy_enum applicationTenancy_d;

    @Override
    public Brand findUsing(final ServiceRegistry2 serviceRegistry) {
        return serviceRegistry.lookupService(BrandRepository.class).findByName(name);
    }

    @Override
    public BrandBuilder builder() {
        return new BrandBuilder()
                .setName(name)
                .setBrandCoverage(coverage)
                .setGroup(group)
                .setPrereq((f,ec) -> f.setCountryOfOrigin(f.objectFor(country_d, ec)))
                .setPrereq((f,ec) -> f.setApplicationTenancy(f.objectFor(applicationTenancy_d, ec)))
                ;
    }

}
