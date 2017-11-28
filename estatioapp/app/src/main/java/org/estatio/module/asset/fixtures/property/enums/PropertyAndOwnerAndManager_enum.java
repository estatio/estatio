package org.estatio.module.asset.fixtures.property.enums;

import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.asset.dom.PropertyType.SHOPPING_CENTER;
import static org.estatio.module.country.fixtures.enums.Country_enum.FRA;
import static org.estatio.module.country.fixtures.enums.Country_enum.GBR;
import static org.estatio.module.country.fixtures.enums.Country_enum.ITA;
import static org.estatio.module.country.fixtures.enums.Country_enum.NLD;
import static org.estatio.module.country.fixtures.enums.Country_enum.SWE;
import static org.incode.module.base.integtests.VT.ld;

//@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum PropertyAndOwnerAndManager_enum /*implements DataEnum<Property, PropertyAndUnitsAndOwnerAndManagerBuilder>*/ {

    BudNl   (Property_enum.BudNl, "BudgetToren", "Amsterdam", NLD, SHOPPING_CENTER,
            ld(2003, 12, 1), ld(2003, 12, 1), "52.37597;4.90814",
            7,
            Organisation_enum.AcmeNl, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JohnDoeNl, null, null
    ) {
    },
    CARTEST (Property_enum.CARTEST, "Centro Carosello test", "Milano", ITA, SHOPPING_CENTER,
            ld(2004, 5, 6), ld(2008, 6, 1), "45.5399865;9.3263305",
            0,
            Organisation_enum.HelloWorldIt, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.LucianoPavarottiIt, null, null
    ),
    GraIt   (Property_enum.GraIt, "Centro Grande Punto", "Milano", ITA, SHOPPING_CENTER,
            ld(2004, 5, 6), ld(2008, 6, 1), "45.5399865;9.3263305",
            55,
            Organisation_enum.HelloWorldIt, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.LucianoPavarottiIt, null, null
    ),
    HanSe   (Property_enum.HanSe, "Handla Center", "Malmo", SWE, SHOPPING_CENTER,
            ld(2004, 5, 6), ld(2008, 6, 1), "56.4916209;13.0074661",
            5,
            Organisation_enum.HelloWorldSe, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.AgnethaFaltskogSe, null, null
    ),
    KalNl   (Property_enum.KalNl, "Kalvertoren", "Amsterdam", NLD, SHOPPING_CENTER,
            ld(2003, 12, 1), ld(2003, 12, 1), "52.37597;4.90814",
            40,
            Organisation_enum.AcmeNl, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JohnDoeNl, null, null
    ),
    MacFr   (Property_enum.MacFr, "Macro", "Paris", FRA, SHOPPING_CENTER,
            ld(2009, 9, 9), ld(2009, 12, 9), "48.745649;2.37957",
            5,
            Organisation_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JeanneDarcFr, null, null
    ),
    MnsFr   (Property_enum.MnsFr, "Minishop", "Paris", FRA, SHOPPING_CENTER,
            ld(2013, 5, 5), ld(2013, 6, 5), "48.923148;2.409439",
            5,
            Organisation_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.FleuretteRenaudFr, null, null
    ),
    OxfGb   (Property_enum.OxfGb, "Oxford Super Mall", "Oxford", GBR, SHOPPING_CENTER,
            ld(1999, 1, 1), ld(2008, 6, 1), "51.74579;-1.24334",
            25,
            Organisation_enum.HelloWorldGb, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.GinoVannelliGb, null, null
    ),
    VivFr   (Property_enum.VivFr, "Vive les shops", "Paris", FRA, SHOPPING_CENTER,
            ld(2004, 5, 6), ld(2008, 6, 1), "48.8740002697085;2.326230019708498",
            5,
            Organisation_enum.HelloWorldFr, ld(1999, 1, 1), ld(2000, 1, 1),
            Person_enum.JeanneDarcFr, null, null
    );

    private final Property_enum property;
    public String getRef() { return property.getRef(); }
    public ApplicationTenancy_enum getApplicationTenancy() { return property.getApplicationTenancy(); }

    private final String name;
    private final String city;
    private final Country_enum country;
    private final PropertyType shoppingCenter;
    private final LocalDate openingDate;
    private final LocalDate acquireDate;
    private final String locationStr;
    private final int numberOfUnits;
    private final Organisation_enum owner;
    private final LocalDate ownerStartDate;
    private final LocalDate ownerEndDate;
    private final Person_enum manager;
    private final LocalDate managerStartDate;
    private final LocalDate managerEndDate;

    PropertyAndOwnerAndManager_enum(
            final Property_enum property,
            final String name,
            final String city,
            final Country_enum country,
            final PropertyType shoppingCenter,
            final LocalDate openingDate,
            final LocalDate acquireDate,
            final String locationStr,
            final int numberOfUnits,
            final Organisation_enum owner,
            final LocalDate ownerStartDate,
            final LocalDate ownerEndDate,
            final Person_enum manager,
            final LocalDate managerStartDate,
            final LocalDate managerEndDate) {

        this.property = property;
        this.name = name;
        this.city = city;
        this.country = country;
        this.shoppingCenter = shoppingCenter;
        this.openingDate = openingDate;
        this.acquireDate = acquireDate;
        this.locationStr = locationStr;

        this.numberOfUnits = numberOfUnits;

        this.owner = owner;
        this.ownerStartDate = ownerStartDate;
        this.ownerEndDate = ownerEndDate;
        this.manager = manager;
        this.managerStartDate = managerStartDate;
        this.managerEndDate = managerEndDate;
    }
}
