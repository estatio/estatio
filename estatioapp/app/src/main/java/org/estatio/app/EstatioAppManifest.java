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

import org.isisaddons.module.pdfbox.dom.PdfBoxModule;
import org.isisaddons.module.security.SecurityModule;

import org.incode.module.base.services.calendar.CalendarService;
import org.incode.module.communications.dom.CommunicationsModule;
import org.incode.module.country.dom.CountryModule;
import org.incode.module.docrendering.stringinterpolator.dom.StringInterpolatorDocRenderingModule;
import org.incode.module.document.dom.DocumentModule;

import org.estatio.dom.EstatioDomainModule;
import org.estatio.domlink.EstatioDomainLinkModule;
import org.estatio.domsettings.EstatioDomainSettingsModule;
import org.estatio.fixture.EstatioFixtureModule;
import org.estatio.fixturescripts.EstatioFixtureScriptsModule;
import org.estatio.numerator.dom.NumeratorDomModule;

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
        modules.addAll(
                Arrays.asList(
                        // TODO: sort out packages for the 'dom' module
                        EstatioDomainModule.class,
                        EstatioDomainLinkModule.class,
                        EstatioDomainSettingsModule.class,

                        NumeratorDomModule.class,

                        CountryModule.class,
                        CommunicationsModule.class,
                        DocumentModule.class,

                        PdfBoxModule.class,
                        StringInterpolatorDocRenderingModule.class,

                        // TODO: sort out packages for the 'fixture' module
                        EstatioFixtureModule.class,
                        EstatioFixtureScriptsModule.class,

                        SecurityModule.class,
                        org.isisaddons.module.command.CommandModule.class,

                        EstatioAppModule.class
                )
        );
        return modules;
    }

    private List<Class<?>> appendAddonModules(List<Class<?>> modules) {
        modules.addAll(
                Arrays.asList(
                        org.isisaddons.module.excel.ExcelModule.class,
                        org.isisaddons.module.poly.PolyModule.class,
                        org.isisaddons.module.sessionlogger.SessionLoggerModule.class,
                        // don't include the settings module, instead we use EstatioDomainSettingsModule
                        // org.isisaddons.module.settings.SettingsModule.class,
                        org.isisaddons.module.stringinterpolator.StringInterpolatorModule.class,
                        org.isisaddons.module.freemarker.dom.XDocReportModule.class
                )
        );
        return modules;
    }

    private List<Class<?>> appendAddonWicketComponents(List<Class<?>> modules) {
        modules.addAll(
                Arrays.asList(
                        // apart from gmap3-service, the other modules here contain no services or entities
                        // but are included "for completeness"
                        org.isisaddons.wicket.excel.cpt.ui.ExcelUiModule.class,
                        org.isisaddons.wicket.fullcalendar2.cpt.ui.FullCalendar2UiModule.class,
                        org.isisaddons.wicket.fullcalendar2.cpt.applib.FullCalendar2ApplibModule.class,
                        org.isisaddons.wicket.gmap3.cpt.applib.Gmap3ApplibModule.class,
                        org.isisaddons.wicket.gmap3.cpt.service.Gmap3ServiceModule.class,
                        org.isisaddons.wicket.gmap3.cpt.ui.Gmap3UiModule.class
                )
        );
        return modules;
    }

    @Override
    public final List<Class<?>> getAdditionalServices() {
        List<Class<?>> additionalServices = Lists.newArrayList();
        additionalServices.addAll(
            Arrays.asList(
                CalendarService.class, // TODO: instead, should have a module for this
                org.isisaddons.module.security.dom.password.PasswordEncryptionServiceUsingJBcrypt.class,
                org.isisaddons.module.security.dom.permission.PermissionsEvaluationServiceAllowBeatsVeto.class
                )
        );
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
