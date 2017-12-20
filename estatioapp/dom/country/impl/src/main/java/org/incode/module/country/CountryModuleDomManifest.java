package org.incode.module.country;

import org.apache.isis.applib.AppManifestAbstract;

import org.incode.module.country.dom.CountryModule;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class CountryModuleDomManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(CountryModule.class);

    public CountryModuleDomManifest() {
        super(BUILDER);
    }


}
