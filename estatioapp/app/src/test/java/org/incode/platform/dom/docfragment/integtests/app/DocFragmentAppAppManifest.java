package org.incode.platform.dom.docfragment.integtests.app;

import org.apache.isis.applib.AppManifestAbstract;

import org.isisaddons.module.freemarker.dom.FreeMarkerModule;

import org.incode.module.docfragment.dom.DocFragmentModule;
import org.incode.platform.dom.docfragment.integtests.app.fixture.DemoAppApplicationModuleFixtureSubmodule;
import org.incode.platform.dom.docfragment.integtests.dom.docfragment.DocFragmentModuleIntegrationSubmodule;

/**
 * Bootstrap the application.
 */
public class DocFragmentAppAppManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            DocFragmentModule.class,
            DocFragmentModuleIntegrationSubmodule.class,
            DemoAppApplicationModuleFixtureSubmodule.class,

            FreeMarkerModule.class  // required by DocFragmentModule, do not yet support transitivity
    );

    public DocFragmentAppAppManifest() {
        super(BUILDER);
    }


}
