package org.incode.module.alias.dom;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Provided for <tt>isis-maven-plugin</tt>.
 */
public class AliasModuleDomManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            AliasModule.class  // domain (entities and repositories)
    );

    public AliasModuleDomManifest() {
        super(BUILDER);
    }

}
