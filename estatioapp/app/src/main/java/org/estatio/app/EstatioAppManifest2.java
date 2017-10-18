package org.estatio.app;

import org.apache.isis.applib.AppManifestAbstract;

public class EstatioAppManifest2 extends AppManifestAbstract {

    public static final Builder BUILDER =
            Builder.forModules(EstatioAppDefn.domModulesAndSecurityAndCommandAddon())
                   .withAdditionalModules(EstatioAppDefn.addonModules())
                   .withAdditionalModules(EstatioAppDefn.addonWicketComponents())
                   .withAdditionalServices(EstatioAppDefn.additionalServices())
                   .withAuthMechanism(null)
                   .withConfigurationPropertiesFile(EstatioAppManifest2.class, "isis-non-changing.properties")
                   .withConfigurationPropertiesFile(EstatioAppManifest2.class, "git.estatio.properties")
            ;

    public EstatioAppManifest2() {
        super(BUILDER);
    }

}
