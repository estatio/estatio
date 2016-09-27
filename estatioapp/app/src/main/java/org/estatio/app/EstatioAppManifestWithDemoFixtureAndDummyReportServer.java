package org.estatio.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.fixturescripts.EstatioDemoFixtureWithDummyReportServer;

public class EstatioAppManifestWithDemoFixtureAndDummyReportServer extends EstatioAppManifest {

    @Override public List<Class<? extends FixtureScript>> getFixtures() {
        return Arrays.asList(
                EstatioDemoFixtureWithDummyReportServer.class
        );
    }

    @Override
    public Map<String, String> getConfigurationProperties() {
        final Map<String, String> props = super.getConfigurationProperties();
        return withInstallFixtures(props);
    }

}
