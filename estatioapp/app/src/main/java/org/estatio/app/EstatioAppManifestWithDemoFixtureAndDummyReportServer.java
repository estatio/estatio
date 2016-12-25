package org.estatio.app;

import java.util.Arrays;
import java.util.Collections;

import org.estatio.fixturescripts.EstatioDemoFixtureWithDummyReportServer;

public class EstatioAppManifestWithDemoFixtureAndDummyReportServer extends EstatioAppManifest {

    public EstatioAppManifestWithDemoFixtureAndDummyReportServer() {
        super(
                Arrays.asList(EstatioDemoFixtureWithDummyReportServer.class),
                null,
                Collections.emptyList()
        );
    }

}
