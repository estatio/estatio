package org.estatio.module.country.fixtures.builder;

import org.incode.module.country.dom.impl.Country;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.estatio.module.country.fixtures.enums.Country_enum;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"data"})
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
