package org.incode.platform.dom.document.integtests.app;

import java.util.List;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.platform.dom.document.integtests.dom.document.fixture.DemoObjectWithUrl_and_OtherObject_and_docrefdata_recreate;

/**
 * Run the app but without setting up any fixtures.
 */
public class DocumentModuleAppManifestWithFixtures extends DocumentModuleAppManifest {

    @Override protected void overrideFixtures(final List<Class<? extends FixtureScript>> fixtureScripts) {
        fixtureScripts.add(DemoObjectWithUrl_and_OtherObject_and_docrefdata_recreate.class);
    }


}
