package org.incode.platform.dom.document.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.document.integtests.demo.dom.order.DemoOrder;
import org.incode.platform.dom.document.integtests.demo.dom.order.DemoOrderLine;

public class DemoOrderAndOrderLine_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(DemoOrderLine.class);
        deleteFrom(DemoOrder.class);
    }

}
