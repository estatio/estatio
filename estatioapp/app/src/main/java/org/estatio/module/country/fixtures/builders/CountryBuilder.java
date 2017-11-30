package org.estatio.module.country.fixtures.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"ref3"}, callSuper = false)
@ToString(of={"ref3"})
@Accessors(chain = true)
public final class CountryBuilder extends BuilderScriptAbstract<Country, CountryBuilder> {

    @Getter @Setter
    String ref3;
    @Getter @Setter
    String ref2;
    @Getter @Setter
    String name;

    @Getter
    private Country object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("ref3", executionContext, String.class);
        checkParam("ref2", executionContext, String.class);
        checkParam("name", executionContext, String.class);

        object = countryRepository.findOrCreateCountry(this.ref3, this.ref2, this.name);

        executionContext.addResult(this, ref3, object);
    }

    @Inject
    CountryRepository countryRepository;
}
