package org.estatio.module.asset.fixtures.property.enums;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.builders.PropertyAndUnitsAndOwnerAndManagerBuilder;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;

//@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum PropertyAndUnitsAndOwnerAndManager_enum implements
        PersonaWithBuilderScript<Property, PropertyAndUnitsAndOwnerAndManagerBuilder>,
        PersonaWithFinder<Property> {

    BudNl   (Property_enum.BudNl,
            7,
            Organisation_enum.AcmeNl, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JohnDoeNl, null, null
    ) {
    },
    RonIt   (Property_enum.RonIt,
            5,
            Organisation_enum.HelloWorldIt, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.RonRondelliIt, null, null
    ),
    GraIt   (Property_enum.GraIt,
            55,
            Organisation_enum.HelloWorldIt, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.LucianoPavarottiIt, null, null
    ),
    HanSe   (Property_enum.HanSe,
            5,
            Organisation_enum.HelloWorldSe, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.AgnethaFaltskogSe, null, null
    ),
    KalNl   (Property_enum.KalNl,
            40,
            Organisation_enum.AcmeNl, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JohnDoeNl, null, null
    ),
    MacFr   (Property_enum.MacFr,
            5,
            Organisation_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JeanneDarcFr, null, null
    ),
    MnsFr   (Property_enum.MnsFr,
            5,
            Organisation_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.FleuretteRenaudFr, null, null
    ),
    OxfGb   (Property_enum.OxfGb,
            25,
            Organisation_enum.HelloWorldGb, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.GinoVannelliGb, null, null
    ),
    VivFr   (Property_enum.VivFr,
            5,
            Organisation_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JeanneDarcFr, null, null
    );

    private final Property_enum property_d;
    public String getRef() { return property_d.getRef(); }

    private final int numberOfUnits;
    private final Organisation_enum owner_d;
    private final LocalDate ownerStartDate;
    private final LocalDate ownerEndDate;
    private final Person_enum manager_d;
    private final LocalDate managerStartDate;
    private final LocalDate managerEndDate;

    PropertyAndUnitsAndOwnerAndManager_enum(
            final Property_enum property_d,
            final int numberOfUnits,
            final Organisation_enum owner_d,
            final LocalDate ownerStartDate,
            final LocalDate ownerEndDate,
            final Person_enum manager_d,
            final LocalDate managerStartDate,
            final LocalDate managerEndDate) {

        this.property_d = property_d;

        this.numberOfUnits = numberOfUnits;

        this.owner_d = owner_d;
        this.ownerStartDate = ownerStartDate;
        this.ownerEndDate = ownerEndDate;
        this.manager_d = manager_d;
        this.managerStartDate = managerStartDate;
        this.managerEndDate = managerEndDate;
    }

    @Override
    public PropertyAndUnitsAndOwnerAndManagerBuilder builder() {
        final Property_enum property_d = getProperty_d();

        return new PropertyAndUnitsAndOwnerAndManagerBuilder()
                .setReference(getRef())

                .setName(property_d.getName())
                .setCity(property_d.getCity())
                .setPrereq((f,ex) -> f.setCountry(f.objectFor(property_d.getCountry_d(), ex)))
                .setPropertyType(property_d.getPropertyType())
                .setOpeningDate(property_d.getOpeningDate())
                .setAcquireDate(property_d.getAcquireDate())
                .setLocationStr(property_d.getLocationStr())

                .setNumberOfUnits(getNumberOfUnits())

                .setPrereq((f,ex) -> f.setOwner(f.objectFor(getOwner_d(), ex)))
                .setOwnerStartDate(getOwnerStartDate())
                .setOwnerEndDate(getOwnerEndDate())

                .setPrereq((f,ex) -> f.setManager(f.objectFor(getManager_d(), ex)))
                .setManagerStartDate(getManagerStartDate())
                .setManagerEndDate(getManagerEndDate());

    }

    @Override
    public Property findUsing(final ServiceRegistry2 serviceRegistry) {
        return serviceRegistry.lookupService(PropertyRepository.class).findPropertyByReference(getRef());
    }
}
