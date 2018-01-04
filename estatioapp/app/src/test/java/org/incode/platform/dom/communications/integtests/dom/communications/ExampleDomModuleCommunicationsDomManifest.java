package org.incode.platform.dom.communications.integtests.dom.communications;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class ExampleDomModuleCommunicationsDomManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(CommunicationsModuleIntegrationSubmodule.class);

    public ExampleDomModuleCommunicationsDomManifest() {
        super(BUILDER);
    }

}
