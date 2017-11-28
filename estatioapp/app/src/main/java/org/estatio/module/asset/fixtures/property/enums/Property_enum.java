package org.estatio.module.asset.fixtures.property.enums;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.base.platform.fixturesupport.EnumWithFinder;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForFr;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGb;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForIt;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNl;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForSe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Property_enum implements EnumWithFinder<Property> {

    BudNl   ("BUD", ApplicationTenancyForNl.data),
    CARTEST ("CAR", ApplicationTenancyForIt.data),
    GraIt   ("GRA", ApplicationTenancyForIt.data),
    HanSe   ("HAN", ApplicationTenancyForSe.data),
    KalNl   ("KAL", ApplicationTenancyForNl.data),
    MacFr   ("MAC", ApplicationTenancyForFr.data),
    MnsFr   ("MNS", ApplicationTenancyForFr.data),
    OxfGb   ("OXF", ApplicationTenancyForGb.data),
    VivFr   ("VIV", ApplicationTenancyForFr.data);

    private final String ref;
    private final ApplicationTenancy_enum applicationTenancy;

    @Override public Property findUsing(final ServiceRegistry2 serviceRegistry) {
        final PropertyRepository repository = serviceRegistry.lookupService(PropertyRepository.class);
        return repository.findPropertyByReference(ref);
    }
}
