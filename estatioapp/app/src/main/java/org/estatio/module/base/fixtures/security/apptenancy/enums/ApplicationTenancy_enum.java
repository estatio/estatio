package org.estatio.module.base.fixtures.security.apptenancy.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.base.platform.fixturesupport.DataEnumPersist;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.base.fixtures.security.apptenancy.builders.ApplicationTenancyBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum ApplicationTenancy_enum
        implements PersonaWithFinder<ApplicationTenancy>,
        PersonaWithBuilderScript<ApplicationTenancy, ApplicationTenancyBuilder> {

    Global      ("/",           "Global"),
    GlobalOnly  ("/_",          "Global only"),

    Fr          ("/FRA",        "France"),
    FrOther     ("/FRA/_",      "France - Other"),
    FrViv       ("/FRA/VIV",    "Vive (FRA)"),
    FrVivDefault("/FRA/VIV/_",  "Vive (FRA) - Default"),
    FrVivTa     ("/FRA/VIV/ta", "Vive (FRA) Tenants Association"),

    Se          ("/SWE",        "Sweden"),
    SeOther     ("/SWE/_",      "Sweden - Other"),
    SeHan       ("/SWE/HAN",    "Handla (SWE)"),
    SeHanDefault("/SWE/HAN/_",  "Handla (SWE) - Default"),
    SeHanTa     ("/SWE/HAN/ta", "Handla (SWE) Tenants Association"),

    Gb          ("/GBR",        "Great Britain"),
    GbOther     ("/GBR/_",      "Great Britain - Other"),
    GbOxf       ("/GBR/OXF",    "Oxford (GBR)"),
    GbOxfDefault("/GBR/OXF/_",  "Oxford (GBR) - Default"),
    GbOxfTa     ("/GBR/OXF/ta", "Oxford (GBR) Tenants Association"),

    Nl          ("/NLD",        "The Netherlands"),
    NlOther     ("/NLD/_",      "The Netherlands - Other"),
    NlKal       ("/NLD/KAL",    "Kalvertoren (NLD)"),
    NlKalDefault("/NLD/KAL/_",  "Kalvertoren (NLD) - Default"),
    NlKalTa     ("/NLD/KAL/ta", "Kalvertoren (NLD) Tenants Association"),

    It          ("/ITA",        "Italy"),
    ItOther     ("/ITA/_",      "Italy - Other"),
    ItGra       ("/ITA/GRA",    "Grande (ITA)"),
    ItGraDefault("/ITA/GRA/_",  "Grande (ITA) - Default"),
    ItGraTa     ("/ITA/GRA/ta", "Grande (ITA) Tenants Association"),
    ;

    private final String path;
    private final String name;

    private String getPathOfParent() {
        final int lastSlash = path.lastIndexOf("/");
        String parentPath = path.substring(0, lastSlash);
        if(parentPath.length() == 0) {
            parentPath = "/";
        }
        return parentPath;
    }

    @Override
    public ApplicationTenancy findUsing(final ServiceRegistry2 serviceRegistry) {
        final ApplicationTenancyRepository repository =
                serviceRegistry.lookupService(ApplicationTenancyRepository.class);
        return repository.findByPath(this.path);
    }

    @Override
    public ApplicationTenancyBuilder toBuilderScript() {
        return new ApplicationTenancyBuilder()
                .setName(name)
                .setPath(path)
                .setPathOfParent(getPathOfParent());
    }

    public static class PersistScript
            extends DataEnumPersist<ApplicationTenancy_enum, ApplicationTenancy, ApplicationTenancyBuilder> {
        public PersistScript() {
            super(ApplicationTenancy_enum.class);
        }
    }


}
