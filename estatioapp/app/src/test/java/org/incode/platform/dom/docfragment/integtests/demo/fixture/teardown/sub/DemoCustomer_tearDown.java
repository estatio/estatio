package org.incode.platform.dom.docfragment.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.docfragment.integtests.demo.dom.customer.DemoCustomer;

public class DemoCustomer_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(DemoCustomer.class);
    }

}
