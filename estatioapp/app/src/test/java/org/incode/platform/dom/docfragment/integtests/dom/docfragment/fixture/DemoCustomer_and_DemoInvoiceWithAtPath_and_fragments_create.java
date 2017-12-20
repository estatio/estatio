package org.incode.platform.dom.docfragment.integtests.dom.docfragment.fixture;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.platform.dom.docfragment.integtests.demo.fixture.data.DemoCustomerData;
import org.incode.platform.dom.docfragment.integtests.demo.fixture.data.DemoInvoiceWithAtPathData;
import org.incode.platform.dom.docfragment.integtests.dom.docfragment.fixture.data.DocFragmentData;

public class DemoCustomer_and_DemoInvoiceWithAtPath_and_fragments_create extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext ec) {

        ec.executeChild(this, new DemoCustomerData.PersistScript());
        ec.executeChild(this, new DemoInvoiceWithAtPathData.PersistScript());
        ec.executeChild(this, new DocFragmentData.PersistScript());

    }
}
