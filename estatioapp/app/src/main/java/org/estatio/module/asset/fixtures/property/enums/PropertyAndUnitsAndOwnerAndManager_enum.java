package org.estatio.module.asset.fixtures.property.enums;

import org.joda.time.LocalDate;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;

//@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum PropertyAndUnitsAndOwnerAndManager_enum /*implements DataEnum<Property, PropertyAndUnitsAndOwnerAndManagerBuilder>*/ {

    BudNl   (Property_enum.BudNl,
            7,
            OrganisationAndComms_enum.AcmeNl, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JohnDoeNl, null, null
    ) {
    },
    CARTEST (Property_enum.CARTEST,
            0,
            OrganisationAndComms_enum.HelloWorldIt, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.LucianoPavarottiIt, null, null
    ),
    GraIt   (Property_enum.GraIt,
            55,
            OrganisationAndComms_enum.HelloWorldIt, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.LucianoPavarottiIt, null, null
    ),
    HanSe   (Property_enum.HanSe,
            5,
            OrganisationAndComms_enum.HelloWorldSe, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.AgnethaFaltskogSe, null, null
    ),
    KalNl   (Property_enum.KalNl,
            40,
            OrganisationAndComms_enum.AcmeNl, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JohnDoeNl, null, null
    ),
    MacFr   (Property_enum.MacFr,
            5,
            OrganisationAndComms_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JeanneDarcFr, null, null
    ),
    MnsFr   (Property_enum.MnsFr,
            5,
            OrganisationAndComms_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.FleuretteRenaudFr, null, null
    ),
    OxfGb   (Property_enum.OxfGb,
            25,
            OrganisationAndComms_enum.HelloWorldGb, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.GinoVannelliGb, null, null
    ),
    VivFr   (Property_enum.VivFr,
            5,
            OrganisationAndComms_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JeanneDarcFr, null, null
    );

    private final Property_enum property_d;
    public String getRef() { return property_d.getRef(); }
    public ApplicationTenancy_enum getApplicationTenancy_d() { return property_d.getCountry_d().getApplicationTenancy_d(); }

    private final int numberOfUnits;
    private final OrganisationAndComms_enum owner_d;
    private final LocalDate ownerStartDate;
    private final LocalDate ownerEndDate;
    private final Person_enum manager_d;
    private final LocalDate managerStartDate;
    private final LocalDate managerEndDate;

    PropertyAndUnitsAndOwnerAndManager_enum(
            final Property_enum property_d,
            final int numberOfUnits,
            final OrganisationAndComms_enum owner_d,
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
}
