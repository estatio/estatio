package org.incode.module.classification.dom;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Provided for <tt>isis-maven-plugin</tt>.
 */
public class ClassificationModuleDomManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(ClassificationModule.class);

    public ClassificationModuleDomManifest() {
        super(BUILDER);
    }
}
