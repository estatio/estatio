package org.estatio.app;

import org.apache.isis.applib.AppManifestAbstract;
import org.apache.isis.applib.AppManifestAbstract2;

import org.estatio.module.application.EstatioApplicationModule;

public class EstatioAppManifest extends AppManifestAbstract {

    public static final AppManifestAbstract2.Builder BUILDER =
            AppManifestAbstract2.Builder.forModule(new EstatioApplicationModule())
                   .withAuthMechanism(null)
                   .withConfigurationPropertiesFile(
                           EstatioAppManifest.class, "isis-non-changing.properties")
                   .withConfigurationPropertiesFile(
                           EstatioAppManifest.class, "git.estatio.properties")
            ;

    public EstatioAppManifest() {
        super(BUILDER);
    }

}
