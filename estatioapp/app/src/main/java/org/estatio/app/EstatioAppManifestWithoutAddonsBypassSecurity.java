package org.estatio.app;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class EstatioAppManifestWithoutAddonsBypassSecurity extends EstatioAppManifest {

    public EstatioAppManifestWithoutAddonsBypassSecurity() {
        super(
                Collections.emptyList(),
                "bypass",
                Collections.emptyList()
        );
    }

    @Override
    public List<Class<?>> getModules() {
        List<Class<?>> modules = Lists.newArrayList();
        appendDomModulesAndSecurityAndCommandAddon(modules);
        return modules;
    }

}
