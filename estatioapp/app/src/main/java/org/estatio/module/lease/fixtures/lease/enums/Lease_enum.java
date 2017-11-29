package org.estatio.module.lease.fixtures.lease.enums;

import org.apache.isis.applib.fixturescripts.EnumWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Lease_enum implements EnumWithFinder<Lease> /*, EnumWithBuilderScript<Lease, LeaseBuilder>*/ {

    KalPoison001Nl  ("KAL-POISON-001"),
    OxfMediaX002Gb  ("OXF-MEDIAX-002"),
    OxfMiracl005Gb  ("OXF-MIRACL-005"),
    OxfPoison003Gb  ("OXF-POISON-003"),
    OxfPret004Gb    ("OXF-PRET-004"),
    OxfTopModel001Gb("OXF-TOPMODEL-001"),
    ;

    private final String ref;

    @Override
    public Lease findUsing(final ServiceRegistry2 serviceRegistry) {
        final LeaseRepository repository = serviceRegistry.lookupService(LeaseRepository.class);
        return repository.findLeaseByReference(ref);
    }

//    @Override
//    public LeaseBuilder toFixtureScript() {
//        return new LeaseBuilder()
//                .setReference(ref)
//                .setName(name)
//                .setCity(city)
//                .setPropertyType(propertyType)
//                .setOpeningDate(openingDate)
//                .setAcquireDate(acquireDate)
//                .setLocationStr(locationStr)
//                .setPrereq((f, ec) -> f.setCountry(f.objectFor(country_d, ec)));
//    }

}
