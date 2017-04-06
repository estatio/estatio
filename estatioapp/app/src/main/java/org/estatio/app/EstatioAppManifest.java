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

import org.incode.module.base.services.calendar.CalendarService;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.fixture.EstatioCapexFixtureModule;
import org.estatio.dom.EstatioDomainModule;
import org.estatio.dom.agreement.EstatioAgreementDomModule;
import org.estatio.dom.asset.EstatioAssetDomModule;
import org.estatio.dom.asset.registration.EstatioRegistrationDomModule;
import org.estatio.dom.assetfinancial.EstatioAssetFinancialDomModule;
import org.estatio.dom.bankmandate.EstatioBankMandateDomModule;
import org.estatio.dom.budgetassignment.EstatioBudgetAssignmentDomModule;
import org.estatio.dom.budgeting.EstatioBudgetingDomModule;
import org.estatio.dom.charge.EstatioChargeDomModule;
import org.estatio.dom.currency.EstatioCurrencyDomModule;
import org.estatio.dom.document.EstatioDocumentDomModule;
import org.estatio.dom.dto.EstatioBaseDtoModule;
import org.estatio.dom.event.EstatioEventDomModule;
import org.estatio.dom.financial.EstatioFinancialDomModule;
import org.estatio.dom.guarantee.EstatioGuaranteeDomModule;
import org.estatio.dom.index.EstatioIndexDomModule;
import org.estatio.dom.invoice.EstatioInvoiceDomModule;
import org.estatio.dom.lease.EstatioLeaseDomModule;
import org.estatio.dom.party.EstatioPartyDomModule;
import org.estatio.dom.tax.EstatioTaxDomModule;
import org.estatio.domlink.EstatioLinkDomModule;
import org.estatio.domsettings.EstatioSettingsDomModule;
import org.estatio.fixture.EstatioFixtureModule;
import org.estatio.fixturescripts.EstatioFixtureScriptsModule;
import org.estatio.lease.fixture.EstatioLeaseFixtureModule;
import org.estatio.numerator.EstatioNumeratorModule;

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

                        // TODO: one day this module will not be required
                        // TODO: ie, once we've renamed all of org.estatio.dom.xxx packages to org.estatio.xxx.dom.
                        EstatioDomainModule.class,

                        // the domain modules.  At the moment these aren't actually required to be registered because we also register EstatioDomainModule (above); but this will change when we sort out the package names for these.
                        EstatioAgreementDomModule.class,
                        EstatioAssetDomModule.class,
                        EstatioAssetFinancialDomModule.class,
                        EstatioBankMandateDomModule.class,
                        EstatioBudgetingDomModule.class,
                        EstatioBudgetAssignmentDomModule.class,
                        EstatioChargeDomModule.class,
                        EstatioCapexDomModule.class,
                        EstatioCurrencyDomModule.class,
                        EstatioDocumentDomModule.class,
                        EstatioEventDomModule.class,
                        EstatioFinancialDomModule.class,
                        EstatioGuaranteeDomModule.class,
                        EstatioIndexDomModule.class,
                        EstatioInvoiceDomModule.class,
                        EstatioLeaseDomModule.class,
                        EstatioLeaseFixtureModule.class,
                        EstatioLinkDomModule.class,
                        EstatioNumeratorModule.class,
                        EstatioPartyDomModule.class,
                        EstatioSettingsDomModule.class,
                        EstatioRegistrationDomModule.class,
                        EstatioTaxDomModule.class,
                        EstatioBaseDtoModule.class,

                        // the incode catalog modules
                        org.incode.module.country.dom.CountryModule.class,
                        org.incode.module.communications.dom.CommunicationsModule.class,
                        org.incode.module.docfragment.dom.DocFragmentModuleDomModule.class,
                        org.incode.module.document.dom.DocumentModule.class,
                        org.incode.module.classification.dom.ClassificationModule.class,

                        // TODO: one day these module may not be required (if we're able to move all the fixtures into the respective modules).
                        EstatioCapexFixtureModule.class,
                        EstatioFixtureModule.class,
                        EstatioFixtureScriptsModule.class,

                        // the technical modules
                        org.isisaddons.module.pdfbox.dom.PdfBoxModule.class,
                        org.incode.module.docrendering.stringinterpolator.dom.StringInterpolatorDocRenderingModule.class,

                        org.isisaddons.module.security.SecurityModule.class,
                        org.isisaddons.module.command.CommandModule.class,
                        org.isisaddons.module.togglz.TogglzModule.class,

                        // for menus etc.
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
                        // don't include the settings module, instead we use EstatioSettingsDomModule
                        // org.isisaddons.module.settings.SettingsModule.class,
                        org.isisaddons.module.stringinterpolator.StringInterpolatorModule.class,
                        org.isisaddons.module.freemarker.dom.FreeMarkerModule.class,
                        org.isisaddons.module.xdocreport.dom.XDocReportModule.class
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
