package org.estatio.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.SecurityModule;

import org.incode.module.doctemplates.dom.DocTemplatesModule;

import org.estatio.canonical.EstatioCanonicalModule;
import org.estatio.dom.EstatioDomainModule;
import org.estatio.domlink.EstatioDomainLinkModule;
import org.estatio.domsettings.EstatioDomainSettingsModule;
import org.estatio.fixture.EstatioFixtureModule;
import org.estatio.fixturescripts.EstatioFixtureScriptsModule;
import org.estatio.services.calendar.CalendarService;

public class EstatioAppManifest implements AppManifest {

    @Override
    public List<Class<?>> getModules() {
        List<Class<?>> modules = Lists.newArrayList();
        appendDomModulesAndSecurityAddon(modules);
        appendAddonModules(modules);
        appendAddonWicketComponents(modules);
        return modules;
    }

    protected List<Class<?>> appendDomModulesAndSecurityAddon(List<Class<?>> modules) {
        modules.addAll(
                Arrays.asList(
                        // TODO: sort out packages for the 'dom' module
                        EstatioCanonicalModule.class,
                        EstatioDomainModule.class,
                        EstatioDomainLinkModule.class,
                        EstatioDomainSettingsModule.class,

                        org.incode.module.doctemplates.dom.DocTemplatesModule.class,

                        // TODO: sort out packages for the 'fixture' module
                        EstatioFixtureModule.class,
                        EstatioFixtureScriptsModule.class,

                        SecurityModule.class,
                        EstatioAppModule.class
                )
        );
        return modules;
    }

    protected List<Class<?>> appendAddonModules(List<Class<?>> modules) {
        modules.addAll(
                Arrays.asList(
                        org.isisaddons.module.excel.ExcelModule.class,
                        org.isisaddons.module.poly.PolyModule.class,
                        org.isisaddons.module.sessionlogger.SessionLoggerModule.class,
                        // don't include the settings module, instead we use EstatioDomainSettingsModule
                        // org.isisaddons.module.settings.SettingsModule.class,
                        org.isisaddons.module.stringinterpolator.StringInterpolatorModule.class,
                        org.isisaddons.module.freemarker.dom.FreeMarkerModule.class
                )
        );
        return modules;
    }

    protected List<Class<?>> appendAddonWicketComponents(List<Class<?>> modules) {
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
    public List<Class<?>> getAdditionalServices() {
        List<Class<?>> additionalServices = Lists.newArrayList();
        appendEstatioCalendarService(additionalServices);
        appendOptionalServicesForSecurityModule(additionalServices);
        return additionalServices;
    }

    protected void appendEstatioCalendarService(final List<Class<?>> additionalServices) {
        // TODO: need to create a module for this (the Estatio ClockService... else maybe use Isis')
        additionalServices.addAll(
                Arrays.asList(
                        CalendarService.class
                )
        );
    }

    protected void appendOptionalServicesForSecurityModule(final List<Class<?>> additionalServices) {
        additionalServices.addAll(
                Arrays.asList(
                        org.isisaddons.module.security.dom.password.PasswordEncryptionServiceUsingJBcrypt.class,
                        org.isisaddons.module.security.dom.permission.PermissionsEvaluationServiceAllowBeatsVeto.class
                )
        );
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
        final Map<String, String> props = Maps.newHashMap();
        appendProps(props);
        return props;
    }

    protected Map<String, String> appendProps(final Map<String, String> props) {
        // Fundamental principle is that we don't allow editing data.
        props.put("isis.objects.editing","false");
        props.put("isis.services.eventbus.implementation", "guava");
        props.put("isis.services.audit.objects", "all");
        props.put("isis.services.eventbus.allowLateRegistration", "true");
        props.put("isis.services.injector.injectPrefix", "true");

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

        props.put("isis.reflector.facet.cssClassFa.patterns",
                Joiner.on(',').join(
                        "new.*:fa-plus",
                        "add.*:fa-plus-square",
                        "create.*:fa-plus",
                        "update.*:fa-edit",
                        "change.*:fa-edit",
                        "maintain.*:fa-edit",
                        "remove.*:fa-minus-square",
                        "copy.*:fa-copy",
                        "move.*:fa-arrow-right",
                        "first.*:fa-star",
                        "find.*:fa-search",
                        "lookup.*:fa-search",
                        "search.*:fa-search",
                        "clear.*:fa-remove",
                        "previous.*:fa-step-backward",
                        "next.*:fa-step-forward",
                        "list.*:fa-list",
                        "all.*:fa-list",
                        "download.*:fa-download",
                        "upload.*:fa-upload",
                        "export.*:fa-exchange",
                        "import.*:fa-exchange",
                        "execute.*:fa-bolt",
                        "run.*:fa-bolt",
                        "calculate.*:fa-calculator",
                        "verify.*:fa-check-circle",
                        "refresh.*:fa-refresh",
                        "install.*:fa-wrench",
                        "stop.*:fa-stop",
                        "terminate.*:fa-stop",
                        "pause.*:fa-pause",
                        "suspend.*:fa-pause",
                        "resume.*:fa-play",
                        "renew.*:fa-repeat",
                        "assign.*:fa-hand-o-right",
                        "approve.*:fa-thumbs-o-up",
                        "decline.*:fa-thumbs-o-down"));

        props.put("isis.reflector.facet.cssClass.patterns",
                Joiner.on(',').join(
                        "update.*:btn-default",
                        "change.*:btn-default",
                        "maintain.*:btn-default",
                        "delete.*:btn-warning",
                        "remove.*:btn-warning"
                        /*,
                        ".*:btn-primary" // this messes up the drop-downs
                        */));

        props.put("isis.reflector.facets.include",
                Joiner.on(',').join(
                        "org.isisaddons.module.security.facets.TenantedAuthorizationFacetFactory",
                        "org.isisaddons.metamodel.paraname8.NamedFacetOnParameterParaname8Factory"));

        return props;
    }

}
