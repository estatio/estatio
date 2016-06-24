package org.estatio.dom;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.datanucleus.PropertyNames;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.SecurityModule;

public class EstatioDomManifest implements AppManifest {

    private static final String DATANUCLEUS_PROPERTIES_ROOT = "isis.persistor.datanucleus.impl.";

    @Override
    public List<Class<?>> getModules() {
        List<Class<?>> modules = Lists.newArrayList();
        modules.add(EstatioDomainModule.class);
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
        final Map<String, String> map = Maps.newHashMap();
        withJavaxJdoRunInMemoryProperties(map);
        withDatanucleusProperties(map);
        withIsisIntegTestProperties(map);
        return map;
    }

    public static Map<String,String> withJavaxJdoRunInMemoryProperties(final Map<String, String> map) {

        map.put(DATANUCLEUS_PROPERTIES_ROOT + "javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:test");
        map.put(DATANUCLEUS_PROPERTIES_ROOT + "javax.jdo.option.ConnectionDriverName", "org.hsqldb.jdbcDriver");
        map.put(DATANUCLEUS_PROPERTIES_ROOT + "javax.jdo.option.ConnectionUserName", "sa");
        map.put(DATANUCLEUS_PROPERTIES_ROOT + "javax.jdo.option.ConnectionPassword", "");

        return map;
    }

    public static Map<String,String> withDatanucleusProperties(final Map<String, String> map) {

        // Don't do validations that consume setup time.
        map.put(DATANUCLEUS_PROPERTIES_ROOT + PropertyNames.PROPERTY_SCHEMA_AUTOCREATE_ALL, "true");
        map.put(DATANUCLEUS_PROPERTIES_ROOT + PropertyNames.PROPERTY_SCHEMA_VALIDATE_ALL, "false");

        // other properties as per WEB-INF/persistor_datanucleus.properties
        map.put(DATANUCLEUS_PROPERTIES_ROOT + "datanucleus.persistenceByReachabilityAtCommit", "false");
        map.put(DATANUCLEUS_PROPERTIES_ROOT + "datanucleus.identifier.case", "MixedCase");
        map.put(DATANUCLEUS_PROPERTIES_ROOT + "datanucleus.cache.level2.type","none");
        map.put(DATANUCLEUS_PROPERTIES_ROOT + "datanucleus.cache.level2.mode","ENABLE_SELECTIVE");

        return map;
    }

    public static Map<String,String> withIsisIntegTestProperties(final Map<String, String> map) {

        // automatically install any fixtures that might have been registered
        map.put("isis.persistor.datanucleus.install-fixtures" , "true");
        map.put("isis.persistor.enforceSafeSemantics", "false");
        map.put("isis.deploymentType", "server_prototype");
        map.put("isis.services.eventbus.allowLateRegistration", "true");

        return map;
    }

}
