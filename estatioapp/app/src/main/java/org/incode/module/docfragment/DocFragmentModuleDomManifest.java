package org.incode.module.docfragment;

import org.apache.isis.applib.AppManifestAbstract;

import org.incode.module.docfragment.dom.DocFragmentModule;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class DocFragmentModuleDomManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            DocFragmentModule.class
    );

    public DocFragmentModuleDomManifest() {
        super(BUILDER);
    }

}
