package org.estatio.module.capex.fixtures.order.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.capex.app.NumeratorForOrdersRepository;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.fixtures.builders.NumeratorBuilder;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum NumeratorForOrder_enum
        implements PersonaWithBuilderScript<Numerator, NumeratorBuilder>, PersonaWithFinder<Numerator> {

    Ita(Country_enum.ITA, "%04d", null),
    ItaScopedToHelloWorldIt(Country_enum.ITA, "%04d", Organisation_enum.HelloWorldIt),
    Fra(Country_enum.FRA, "%05d", null),
    ;

    private final Country_enum country_d;
    private final String format;
    private final Organisation_enum organisation_d;


    @Override
    public NumeratorBuilder builder() {
        return new NumeratorBuilder()
                .setName(NumeratorForOrdersRepository.NUMERATOR_NAME)
                .setPrereq((f, ec) -> f.setCountry(f.objectFor(country_d, ec)))
                .setPrereq((f, ec) -> f.setScopedTo(f.objectFor(organisation_d, ec)))
                .setFormat(format)
                ;
    }

    @Override
    public Numerator findUsing(final ServiceRegistry2 serviceRegistry) {
        final NumeratorForOrdersRepository numeratorForOrdersRepository = serviceRegistry
                .lookupService(NumeratorForOrdersRepository.class);

        final Country country = country_d.findUsing(serviceRegistry);
        final Organisation scopedToIfAny = organisation_d == null
                ? null
                : organisation_d.findUsing(serviceRegistry);

        return numeratorForOrdersRepository.findNumerator(country, scopedToIfAny);
    }


}
