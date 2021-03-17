package org.incode.platform.dom.document.integtests.dom.document.fixture;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.platform.dom.document.integtests.demo.fixture.setup.DemoObjectWithUrl_createUpTo5_fakeData;
import org.incode.platform.dom.document.integtests.demo.fixture.setup.OtherObject_createUpTo5_fakeData;
import org.incode.platform.dom.document.integtests.dom.document.fixture.seed.DocumentTypeAndTemplatesApplicableForDemoObjectFixture;

public class DemoObjectWithUrl_and_OtherObject_and_docrefdata_create extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext ec) {

        ec.executeChild(this, new DocumentTypeAndTemplatesApplicableForDemoObjectFixture());

        ec.executeChild(this, new DemoObjectWithUrl_createUpTo5_fakeData());
        ec.executeChild(this, new OtherObject_createUpTo5_fakeData());
    }


}
