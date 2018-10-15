package org.estatio.module.numerator.fixtures.builders;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of = { "country" }, callSuper = false)
@ToString(of = { "country" })
@Accessors(chain = true)
public final class NumeratorBuilder extends BuilderScriptAbstract<Numerator, NumeratorBuilder> {

    @Getter @Setter
    Country country;

    @Getter @Setter
    String format;

    @Getter @Setter
    String name;

    @Getter
    Numerator object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("country", executionContext, Country.class);
        checkParam("format", executionContext, String.class);
        checkParam("name", executionContext, String.class);

        Numerator numerator = numeratorRepository
                .createGlobalNumerator(name, format, BigInteger.ZERO, estatioApplicationTenancyRepository.findOrCreateTenancyFor(country));

        executionContext.addResult(this, name, numerator);

        object = numerator;
    }

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

}
