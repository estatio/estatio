package org.estatio.module.party.fixtures.numerator.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;

import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.fixtures.builders.NumeratorBuilder;
import org.estatio.module.party.dom.PartyConstants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum NumeratorForOrganisation_enum
        implements PersonaWithBuilderScript<Numerator, NumeratorBuilder> {

    Fra(Country_enum.FRA, "FRCL%04d", PartyConstants.ORGANISATION_REFERENCE_NUMERATOR_NAME),;

    private final Country_enum country_d;
    private final String format;
    private final String name;

    @Override
    public NumeratorBuilder builder() {
        return new NumeratorBuilder()
                .setPrereq((f, ec) -> f.setCountry(f.objectFor(country_d, ec)))
                .setFormat(format)
                .setName(name);
    }

}
