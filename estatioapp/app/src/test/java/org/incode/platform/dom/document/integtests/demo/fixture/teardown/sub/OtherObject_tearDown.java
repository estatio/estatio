package org.incode.platform.dom.document.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.document.integtests.demo.dom.other.OtherObject;

public class OtherObject_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(OtherObject.class);
    }


}
