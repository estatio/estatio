package org.incode.platform.dom.alias.integtests.app;

import org.apache.isis.applib.AppManifestAbstract;

import org.incode.module.alias.dom.AliasModule;
import org.incode.platform.dom.alias.integtests.dom.alias.ExampleDomModuleAliasModule;

/**
 * Bootstrap the application.
 */
public class AliasModuleAppManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            AliasModule.class, // dom module
            ExampleDomModuleAliasModule.class,
            AliasAppModule.class
    );

    public AliasModuleAppManifest() {
        super(BUILDER);
    }

}
