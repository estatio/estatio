package org.estatio.app;

import java.util.List;

import com.google.common.collect.Lists;

public class EstatioAppManifestWithoutAddonsBypassSecurity extends EstatioAppManifest {

    @Override
    public List<Class<?>> getModules() {
        List<Class<?>> modules = Lists.newArrayList();
        appendDomModulesAndSecurityAndCommandAddon(modules);
        return modules;
    }

    @Override public String getAuthenticationMechanism() {
        return "bypass";
    }

    @Override public String getAuthorizationMechanism() {
        return "bypass";
    }
}
