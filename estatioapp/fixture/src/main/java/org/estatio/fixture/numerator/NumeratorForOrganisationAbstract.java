package org.estatio.fixture.numerator;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.country.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.numerator.dom.Numerator;
import org.estatio.numerator.dom.NumeratorRepository;

public abstract class NumeratorForOrganisationAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected Numerator createNumeratorForOrganisation(
            String name,
            String format,
            Country country,
            ExecutionContext executionContext) {

        Numerator numerator = numeratorRepository
                .createGlobalNumerator(name, format, BigInteger.ZERO,  estatioApplicationTenancyRepository.findOrCreateTenancyFor(country));
        return executionContext.addResult(this, numerator);
    }

    @Inject
    protected NumeratorRepository numeratorRepository;

    @Inject
    protected EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

}
