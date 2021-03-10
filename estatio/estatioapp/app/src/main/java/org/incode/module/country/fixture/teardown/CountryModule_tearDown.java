package org.incode.module.country.fixture.teardown;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;

public class CountryModule_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(ExecutionContext executionContext) {
        deleteFrom(State.class);
        deleteFrom(Country.class);
    }


}
