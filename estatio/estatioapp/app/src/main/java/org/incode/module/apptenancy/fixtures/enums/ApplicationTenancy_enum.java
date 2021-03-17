package org.incode.module.apptenancy.fixtures.enums;

import java.util.regex.Pattern;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.fixturescripts.setup.PersonaEnumPersistAll;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.apptenancy.fixtures.builders.ApplicationTenancyBuilder;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public enum ApplicationTenancy_enum
        implements PersonaWithFinder<ApplicationTenancy>,
        PersonaWithBuilderScript<ApplicationTenancy, ApplicationTenancyBuilder> {

    Global      ("/",           "Global"),
    GlobalOnly  ("/_",          "Global only"),

    Fr          ("/FRA",        "France"),
    FrBe        ("/FRA;/BEL",   "France - Belgium"), //Since ECP-677
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
    private final boolean countryLevel;

    ApplicationTenancy_enum(final String path, final String name) {
        this.path = path;
        this.name = name;
        this.countryLevel = Pattern.compile("^/[^/]{3}$").matcher(path).matches();
    }

    public boolean isCountryLevel() {
        return countryLevel;
    }

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
    public ApplicationTenancyBuilder builder() {
        return new ApplicationTenancyBuilder()
                .setName(name)
                .setPath(path)
                .setPathOfParent(getPathOfParent());
    }

    @Programmatic
    public static class PersistAll
            extends PersonaEnumPersistAll<ApplicationTenancy_enum, ApplicationTenancy, ApplicationTenancyBuilder> {
        public PersistAll() {
            super(ApplicationTenancy_enum.class);
        }
    }


}
