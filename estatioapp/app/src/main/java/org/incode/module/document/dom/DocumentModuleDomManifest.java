package org.incode.module.document.dom;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Provided for <tt>isis-maven-plugin</tt>.
 */
public class DocumentModuleDomManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(DocumentModule.class);

    public DocumentModuleDomManifest() {
        super(BUILDER);
    }

}
