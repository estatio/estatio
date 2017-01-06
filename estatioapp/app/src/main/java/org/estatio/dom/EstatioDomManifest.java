package org.estatio.dom;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.SecurityModule;

import org.estatio.domsettings.EstatioDomainSettingsModule;

public class EstatioDomManifest implements AppManifest {

    @Override
    public List<Class<?>> getModules() {
        List<Class<?>> modules = Lists.newArrayList();
        modules.add(EstatioDomainModule.class);
        modules.add(EstatioDomainSettingsModule.class);
        modules.add(SecurityModule.class);
        return modules;
    }

    @Override
    public List<Class<?>> getAdditionalServices() {
        return Collections.emptyList();
    }

    @Override
    public String getAuthenticationMechanism() {
        return null;
    }

    @Override
    public String getAuthorizationMechanism() {
        return null;
    }

    @Override
    public List<Class<? extends FixtureScript>> getFixtures() {
        return null;
    }

    @Override
    public Map<String, String> getConfigurationProperties() {

        // need to set up in-memory database, because the @PostConstruct on some of the
        // seed domain services expects there to be a database available
        final Map<String, String> map = Maps.newHashMap();
        AppManifest.Util.withJavaxJdoRunInMemoryProperties(map);
        AppManifest.Util.withDataNucleusProperties(map);
        AppManifest.Util.withIsisIntegTestProperties(map);
        return map;
    }

}
