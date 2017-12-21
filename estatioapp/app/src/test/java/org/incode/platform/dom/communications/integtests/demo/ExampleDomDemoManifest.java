package org.incode.platform.dom.communications.integtests.demo;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class ExampleDomDemoManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            CommunicationsModuleDemoDomSubmodule.class
    );

    public ExampleDomDemoManifest() {
        super(BUILDER);
    }


}
