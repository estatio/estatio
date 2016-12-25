package org.estatio.app;

import java.util.Arrays;
import java.util.Collections;

import org.estatio.fixturescripts.EstatioDemoFixture;

public class EstatioAppManifestWithDemoFixture extends EstatioAppManifest {

    public EstatioAppManifestWithDemoFixture() {
        super(
                Arrays.asList(EstatioDemoFixture.class),
                null,
                Collections.emptyList()
        );
    }

}
