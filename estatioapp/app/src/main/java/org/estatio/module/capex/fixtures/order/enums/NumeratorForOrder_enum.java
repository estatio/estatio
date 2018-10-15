package org.estatio.module.capex.fixtures.order.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;

import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.fixtures.builders.NumeratorBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum NumeratorForOrder_enum
        implements PersonaWithBuilderScript<Numerator, NumeratorBuilder> {

    Ita(Country_enum.ITA, "%04d", "Order number"),
    Fra(Country_enum.FRA, "%05d", "Order number"),
    ;

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
