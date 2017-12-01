package org.estatio.module.asset.fixtures.property.enums;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.asset.fixtures.property.builders.PropertyBuilder;
import org.estatio.module.country.fixtures.enums.Country_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.asset.dom.PropertyType.SHOPPING_CENTER;
import static org.estatio.module.country.fixtures.enums.Country_enum.FRA;
import static org.estatio.module.country.fixtures.enums.Country_enum.GBR;
import static org.estatio.module.country.fixtures.enums.Country_enum.ITA;
import static org.estatio.module.country.fixtures.enums.Country_enum.NLD;
import static org.estatio.module.country.fixtures.enums.Country_enum.SWE;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Property_enum implements PersonaWithFinder<Property>, PersonaWithBuilderScript<Property, PropertyBuilder> {

    BudNl   ("BUD", "BudgetToren", "Amsterdam", NLD, SHOPPING_CENTER,
            ld(2003, 12, 1), ld(2003, 12, 1), "52.37597;4.90814"),
    CARTEST ("CAR", "Centro Carosello test", "Milano", ITA, SHOPPING_CENTER,
            ld(2004, 5, 6), ld(2008, 6, 1), "45.5399865;9.3263305"),
    GraIt   ("GRA", "Centro Grande Punto", "Milano", ITA, SHOPPING_CENTER,
            ld(2004, 5, 6), ld(2008, 6, 1), "45.5399865;9.3263305"),
    HanSe   ("HAN", "Handla Center", "Malmo", SWE, SHOPPING_CENTER,
            ld(2004, 5, 6), ld(2008, 6, 1), "56.4916209;13.0074661"),
    KalNl   ("KAL", "Kalvertoren", "Amsterdam", NLD, SHOPPING_CENTER,
            ld(2003, 12, 1), ld(2003, 12, 1), "52.37597;4.90814"),
    MacFr   ("MAC", "Macro", "Paris", FRA, SHOPPING_CENTER,
            ld(2009, 9, 9), ld(2009, 12, 9), "48.745649;2.37957"),
    MnsFr   ("MNS", "Minishop", "Paris", FRA, SHOPPING_CENTER,
            ld(2013, 5, 5), ld(2013, 6, 5), "48.923148;2.409439"),
    OxfGb   ("OXF", "Oxford Super Mall", "Oxford", GBR, SHOPPING_CENTER,
            ld(1999, 1, 1), ld(2008, 6, 1), "51.74579;-1.24334"),
    VivFr   ("VIV", "Vive les shops", "Paris", FRA, SHOPPING_CENTER,
            ld(2004, 5, 6), ld(2008, 6, 1), "48.8740002697085;2.326230019708498");

    private final String ref;
    private final String name;
    private final String city;
    private final Country_enum country_d;
    private final PropertyType propertyType;
    private final LocalDate openingDate;
    private final LocalDate acquireDate;
    private final String locationStr;

    @Override
    public Property findUsing(final ServiceRegistry2 serviceRegistry) {
        final PropertyRepository repository = serviceRegistry.lookupService(PropertyRepository.class);
        return repository.findPropertyByReference(ref);
    }

    @Override
    public PropertyBuilder toBuilderScript() {
        return new PropertyBuilder()
                .setReference(ref)
                .setName(name)
                .setCity(city)
                .setPropertyType(propertyType)
                .setOpeningDate(openingDate)
                .setAcquireDate(acquireDate)
                .setLocationStr(locationStr)
                .setPrereq((f, ec) -> f.setCountry(f.objectFor(country_d, ec)));
    }

    public String unitRef(final String suffix) {
        return getRef() + "-" + suffix;
    }
}
