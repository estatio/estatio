package org.estatio.module.country.fixtures.builder;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.platform.fixturesupport.BuilderScriptAbstract;
import org.estatio.module.country.fixtures.enums.Country_enum;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class CountryBuilder extends BuilderScriptAbstract<CountryBuilder> {

    @Getter @Setter
    Country_enum data;

    @Getter
    private Country country;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("data", executionContext, Country_enum.class);

        country = data.upsertUsing(serviceRegistry);

        executionContext.addResult(this, data.getRef3(), country);
    }
}