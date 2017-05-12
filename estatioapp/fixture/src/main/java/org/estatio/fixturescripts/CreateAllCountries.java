package org.estatio.fixturescripts;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.incode.module.country.fixture.AllCountries;

public class CreateAllCountries extends DiscoverableFixtureScript {
    @Override protected void execute(final ExecutionContext executionContext) {
        executionContext.executeChild(this, new AllCountries());
    }
}
