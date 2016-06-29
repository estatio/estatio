package org.estatio.fixture.numerator;

import java.math.BigInteger;

import javax.inject.Inject;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.geography.Country;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.Numerators;
import org.estatio.fixture.EstatioFixtureScript;


public abstract class NumeratorForOrganisationAbstract extends EstatioFixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected Numerator createNumeratorForOrganisation(
            String name,
            String format,
            Country country,
            ExecutionContext executionContext) {

        Numerator numerator = numerators.createGlobalNumerator(name, format, BigInteger.ZERO,  estatioApplicationTenancyRepository.findOrCreateTenancyFor(country));
        return executionContext.addResult(this, numerator);
    }

    @Inject
    protected Numerators numerators;

    @Inject
    protected EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

}
