package org.estatio.app;

import org.apache.isis.applib.AppManifestAbstract;

public class EstatioAppManifest2 extends AppManifestAbstract {

    public static final Builder BUILDER =
            Builder.forModules(EstatioAppManifest.domModulesAndSecurityAndCommandAddon())
                   .withAdditionalModules(EstatioAppManifest.addonModules())
                   .withAdditionalModules(EstatioAppManifest.addonWicketComponents())
                   .withAdditionalServices(EstatioAppManifest.additionalServices())
                   .withAuthMechanism(null)
                   .withConfigurationPropertiesFile(EstatioAppManifest.class, "isis-non-changing.properties")
            ;

    public EstatioAppManifest2() {
        super(BUILDER);
    }

}
