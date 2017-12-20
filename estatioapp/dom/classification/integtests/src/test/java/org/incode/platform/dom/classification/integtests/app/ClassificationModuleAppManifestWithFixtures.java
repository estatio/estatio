package org.incode.platform.dom.classification.integtests.app;

import java.util.List;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;

/**
 * Run the app but without setting up any fixtures.
 */
public class ClassificationModuleAppManifestWithFixtures extends ClassificationModuleAppManifest {

    @Override protected void overrideFixtures(final List<Class<? extends FixtureScript>> fixtureScripts) {
        fixtureScripts.add(DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3.class);
    }


}
