package org.estatio.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.audit.AuditModule;
import org.isisaddons.module.command.CommandModule;
import org.isisaddons.module.devutils.DevUtilsModule;
import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.poly.dom.PolyModule;
import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.sessionlogger.SessionLoggerModule;
import org.isisaddons.module.settings.SettingsModule;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.estatio.dom.EstatioDomainModule;
import org.estatio.canonicalmappings.EstatioCanonicalMappingsModule;
import org.estatio.domlink.EstatioDomainLinkModule;
import org.estatio.domsettings.EstatioDomainSettingsModule;
import org.estatio.fixturescripts.EstatioFixtureScriptsModule;

public class EstatioAppManifest implements AppManifest {

    @Override
    public List<Class<?>> getModules() {
        List<Class<?>> modules = Lists.newArrayList();
        appendDomModulesAndSecurityAddon(modules);
        appendAddonModules(modules);
        return modules;
    }

    protected List<Class<?>> appendAddonModules(List<Class<?>> modules) {
        modules.addAll(
                Arrays.asList(
                PolyModule.class,
                SettingsModule.class,

                AuditModule.class,
                CommandModule.class,
                DevUtilsModule.class,
                SessionLoggerModule.class

                // TODO: stringinterpolator-module
                // TODO: excel-dom

                // fullcalendar2-cpt doesn't need a module
                // TODO: gmap3-cpt DOES need a module

                // TODO: excel-cpt
                )
        );
        return modules;
    }

    protected List<Class<?>> appendDomModulesAndSecurityAddon(List<Class<?>> modules) {
        modules.addAll(
                    Arrays.asList(
                    EstatioDomainModule.class,
                    EstatioDomainLinkModule.class,
                    EstatioDomainSettingsModule.class,
                    EstatioFixtureScriptsModule.class, // TODO: need to sort out the packages for this module, are breaking our own conventions
                    EstatioCanonicalMappingsModule.class,
                    SecurityModule.class,
                    EstatioAppModule.class
                )
        );
        return modules;
    }

    @Override public List<Class<?>> getAdditionalServices() {
        return Arrays.asList(
                ExcelService.class, // TODO: missing a module to reference
                StringInterpolatorService.class, // TODO: missing a module to reference
                org.isisaddons.module.security.dom.password.PasswordEncryptionServiceUsingJBcrypt.class,
                org.isisaddons.module.security.dom.permission.PermissionsEvaluationServiceAllowBeatsVeto.class
        );
    }

    @Override public String getAuthenticationMechanism() {
        return null;
    }

    @Override public String getAuthorizationMechanism() {
        return null;
    }

    @Override public List<Class<? extends FixtureScript>> getFixtures() {
        return Arrays.asList(
                org.estatio.fixture.EstatioDemoFixture.class
        );
    }

    @Override public Map<String, String> getConfigurationProperties() {
        final HashMap<String, String> props = Maps.newHashMap();
        props.put("isis.services.audit.objects","all");

        props.put("isis.persistor.datanucleus.install-fixtures", "true");

        // uncomment to use log4jdbc instead
        // props.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
        // "net.sf.log4jdbc.DriverSpy");

        // props.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL",
        // "jdbc:hsqldb:mem:test;sqllog=3");

        //
        // props.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL",
        // "jdbc:sqlserver://localhost:1433;instance=.;databaseName=estatio");
        // props.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
        // "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        // props.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionUserName",
        // "estatio");
        // props.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionPassword",
        // "estatio");


        return props;
    }
}
