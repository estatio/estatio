package org.estatio.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class EstatioAppManifest implements AppManifest {

    private final List<Class<? extends FixtureScript>> fixtureScripts;
    private final String authMechanism;
    private final List<Class<?>> additionalModules;

    public EstatioAppManifest() {
        this(
                Collections.emptyList(),
                null,
                Collections.emptyList()
        );
    }

    public EstatioAppManifest(
            final List<Class<? extends FixtureScript>> fixtureScripts,
            final String authMechanism,
            final List<Class<?>> additionalModules) {
        this.fixtureScripts = elseNullIfEmpty(fixtureScripts);
        this.authMechanism = authMechanism;
        this.additionalModules = elseNullIfEmpty(additionalModules);
    }

    static <T> List<T> elseNullIfEmpty(final List<T> list) {
        return list != null && list.isEmpty()
                ? null
                : list;
    }

    @Override
    public List<Class<?>> getModules() {
        List<Class<?>> modules = Lists.newArrayList();
        appendDomModulesAndSecurityAndCommandAddon(modules);
        appendAddonModules(modules);
        appendAddonWicketComponents(modules);
        appendAdditionalModules(modules);
        return modules;
    }

    private void appendAdditionalModules(final List<Class<?>> modules) {
        if(additionalModules == null) {
            return;
        }
        modules.addAll(additionalModules);
    }

    protected List<Class<?>> appendDomModulesAndSecurityAndCommandAddon(List<Class<?>> modules) {
        modules.addAll(Arrays.asList(EstatioAppDefn.domModulesAndSecurityAndCommandAddon()));
        return modules;
    }

    private List<Class<?>> appendAddonModules(List<Class<?>> modules) {
        modules.addAll(Arrays.asList(EstatioAppDefn.addonModules()));
        return modules;
    }

    private List<Class<?>> appendAddonWicketComponents(List<Class<?>> modules) {
        modules.addAll(Arrays.asList(EstatioAppDefn.addonWicketComponents()));
        return modules;
    }

    @Override
    public final List<Class<?>> getAdditionalServices() {
        List<Class<?>> additionalServices = Lists.newArrayList();
        additionalServices.addAll(Arrays.asList(EstatioAppDefn.additionalServices()));
        return additionalServices;
    }

    @Override
    public final String getAuthenticationMechanism() {
        return authMechanism;
    }

    @Override
    public final String getAuthorizationMechanism() {
        return authMechanism;
    }

    @Override
    public List<Class<? extends FixtureScript>> getFixtures() {
        return fixtureScripts;
    }

    @Override
    public Map<String, String> getConfigurationProperties() {
        final Map<String, String> props = Maps.newHashMap();

        loadPropsInto(props, "isis-non-changing.properties");

        if(fixtureScripts != null) {
            withInstallFixtures(props);
        }

        return props;
    }

    static void loadPropsInto(final Map<String, String> props, final String propertiesFile) {
        final Properties properties = new Properties();
        try {
            try (final InputStream stream =
                    EstatioAppManifest.class.getResourceAsStream(propertiesFile)) {
                properties.load(stream);
                for (Object key : properties.keySet()) {
                    final Object value = properties.get(key);
                    if(key instanceof String && value instanceof String) {
                        props.put((String)key, (String)value);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Failed to load '%s' file ", propertiesFile), e);
        }
    }


    private static Map<String, String> withInstallFixtures(Map<String, String> props) {
        props.put("isis.persistor.datanucleus.install-fixtures", "true");
        return props;
    }


}
