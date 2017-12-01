package org.estatio.module.party.fixtures.numerator.builders;

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

@EqualsAndHashCode(of={"name", "format", "country"}, callSuper = false)
@ToString(of={"name", "format", "country"})
@Accessors(chain = true)
public final class NumeratorForOrganisationBuilder
        extends BuilderScriptAbstract<Numerator, NumeratorForOrganisationBuilder> {

    @Getter @Setter
    String name;

    @Getter @Setter
    String format;

    @Getter @Setter
    Country country;

    @Getter
    Numerator object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("name", executionContext, String.class);
        checkParam("format", executionContext, String.class);
        checkParam("country", executionContext, Country.class);

        Numerator numerator = numeratorRepository
                .createGlobalNumerator(name, format, BigInteger.ZERO,  estatioApplicationTenancyRepository.findOrCreateTenancyFor(country));

        executionContext.addResult(this, name, numerator);

        object = numerator;
    }

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

}
