package org.estatio.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.fixture.budget.spreadsheets.Budget2014FixtureForOxfFromSpreadsheet;
import org.estatio.fixturescripts.EstatioDemoFixture;

public class EstatioAppManifestWithDemoExcelFixture extends EstatioAppManifest {

    @Override public List<Class<? extends FixtureScript>> getFixtures() {
        return Arrays.asList(
                EstatioDemoFixture.class,
                Budget2014FixtureForOxfFromSpreadsheet.class
        );
    }

    @Override
    public Map<String, String> getConfigurationProperties() {
        final Map<String, String> props = super.getConfigurationProperties();
        return withInstallFixtures(props);
    }

}
