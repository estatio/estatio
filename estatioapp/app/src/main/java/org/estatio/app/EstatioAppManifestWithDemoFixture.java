package org.estatio.app;

import java.util.List;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.fixturescripts.EstatioDemoFixture;

public class EstatioAppManifestWithDemoFixture extends EstatioAppManifest2 {

    @Override protected void overrideFixtures(final List<Class<? extends FixtureScript>> fixtureScripts) {
        fixtureScripts.add(EstatioDemoFixture.class);
    }


}
