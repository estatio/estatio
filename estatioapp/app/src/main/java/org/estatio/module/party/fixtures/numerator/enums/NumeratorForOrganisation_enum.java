package org.estatio.module.party.fixtures.numerator.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;

import org.incode.module.country.fixtures.enums.Country_enum;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.fixtures.numerator.builders.NumeratorForOrganisationBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum NumeratorForOrganisation_enum
        implements PersonaWithBuilderScript<Numerator, NumeratorForOrganisationBuilder> {

    Fra(Country_enum.FRA, "FRCL%04d"),
    ;

    private final Country_enum country_d;
    private final String format;


    @Override
    public NumeratorForOrganisationBuilder builder() {
        return new NumeratorForOrganisationBuilder()
                .setPrereq((f,ec) -> f.setCountry(f.objectFor(country_d, ec)))
                .setFormat(format);
    }


}
