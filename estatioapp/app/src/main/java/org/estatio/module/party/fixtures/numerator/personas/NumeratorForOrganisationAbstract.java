package org.estatio.module.party.fixtures.numerator.personas;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.fixtures.numerator.builders.NumeratorForOrganisationBuilder;

public abstract class NumeratorForOrganisationAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected Numerator createNumeratorForOrganisation(
            String name,
            String format,
            Country country,
            ExecutionContext executionContext) {

        final NumeratorForOrganisationBuilder numeratorForOrganisationBuilder = new NumeratorForOrganisationBuilder();
        final Numerator numerator = numeratorForOrganisationBuilder
                .setName(name)
                .setFormat(format)
                .setCountry(country)
                .build(this, executionContext)
                .getNumerator();

//        Numerator numerator = numeratorRepository
//                .createGlobalNumerator(name, format, BigInteger.ZERO,  estatioApplicationTenancyRepository.findOrCreateTenancyFor(country));
//        return executionContext.addResult(this, numerator);

        return numerator;
    }

//    @Inject
//    protected NumeratorRepository numeratorRepository;
//
//    @Inject
//    protected EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

}
