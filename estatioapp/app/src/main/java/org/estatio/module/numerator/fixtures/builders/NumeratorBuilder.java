package org.estatio.module.numerator.fixtures.builders;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

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

    @Getter @Setter
    Object scopedTo;

    @Getter @Setter
    Object scopedTo2;

    @Getter
    Numerator object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("country", executionContext, Country.class);
        checkParam("format", executionContext, String.class);
        checkParam("name", executionContext, String.class);

        final ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);

        final Numerator numerator = numeratorRepository.findOrCreate(
                name, country, scopedTo, scopedTo2, format, BigInteger.ZERO, applicationTenancy);

        executionContext.addResult(this, name, numerator);

        object = numerator;
    }

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;


}
