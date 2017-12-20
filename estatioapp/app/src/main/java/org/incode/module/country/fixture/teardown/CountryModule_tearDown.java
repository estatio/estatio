package org.incode.module.country.fixture.teardown;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class CountryModule_tearDown extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"incodeCountry\".\"State\"");
        isisJdoSupport.executeUpdate("delete from \"incodeCountry\".\"Country\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
