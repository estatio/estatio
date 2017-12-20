package org.incode.module.docfragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.AppManifestAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.docfragment.dom.DocFragmentModuleDomModule;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class DocFragmentModuleDomManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            DocFragmentModuleDomModule.class
    );

    public DocFragmentModuleDomManifest() {
        super(BUILDER);
    }

}
