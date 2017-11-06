package org.estatio.app;

import org.incode.module.base.services.calendar.CalendarService;

import org.estatio.capex.EstatioCapexModule;
import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.fixture.EstatioCapexFixtureModule;
import org.estatio.charge.EstatioChargeModule;
import org.estatio.dom.EstatioDomainModule;
import org.estatio.dom.agreement.EstatioAgreementDomModule;
import org.estatio.dom.asset.EstatioAssetDomModule;
import org.estatio.module.registration.EstatioRegistrationModule;
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
import org.estatio.module.tax.EstatioTaxModule;
import org.estatio.domlink.EstatioLinkDomModule;
import org.estatio.module.settings.EstatioSettingsModule;
import org.estatio.fixture.EstatioFixtureModule;
import org.estatio.fixturescripts.EstatioFixtureScriptsModule;
import org.estatio.lease.fixture.EstatioLeaseFixtureModule;
import org.estatio.numerator.EstatioNumeratorModule;

class EstatioAppDefn  {

    private EstatioAppDefn(){}

    static Class<?>[] domModulesAndSecurityAndCommandAddon() {
        return new Class<?>[] {

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
                EstatioChargeModule.class,
                EstatioChargeDomModule.class,
                EstatioCapexModule.class,
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
                EstatioSettingsModule.class,
                EstatioRegistrationModule.class,
                EstatioTaxModule.class,
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
                EstatioAppModule.class };
    }

    static Class<?>[] addonModules() {
        return new Class<?>[] { org.isisaddons.module.excel.ExcelModule.class,
                org.isisaddons.module.servletapi.ServletApiModule.class,
                org.isisaddons.module.poly.PolyModule.class,
                org.isisaddons.module.sessionlogger.SessionLoggerModule.class,
                // don't include the settings module, instead we use EstatioSettingsModule
                // org.isisaddons.module.settings.SettingsModule.class,
                org.isisaddons.module.stringinterpolator.StringInterpolatorModule.class,
                org.isisaddons.module.freemarker.dom.FreeMarkerModule.class,
                org.isisaddons.module.xdocreport.dom.XDocReportModule.class };
    }

    static Class<?>[] addonWicketComponents() {
        return new Class<?>[] {
                // apart from gmap3-service, the other modules here contain no services or entities
                // but are included "for completeness"
                org.isisaddons.wicket.excel.cpt.ui.ExcelUiModule.class,
                org.isisaddons.wicket.fullcalendar2.cpt.ui.FullCalendar2UiModule.class,
                org.isisaddons.wicket.fullcalendar2.cpt.applib.FullCalendar2ApplibModule.class,
                org.isisaddons.wicket.gmap3.cpt.applib.Gmap3ApplibModule.class,
                org.isisaddons.wicket.gmap3.cpt.service.Gmap3ServiceModule.class,
                org.isisaddons.wicket.gmap3.cpt.ui.Gmap3UiModule.class };
    }

    static Class<?>[] additionalServices() {
        return new Class<?>[] {
                CalendarService.class, // TODO: instead, should have a module for this
                org.isisaddons.module.security.dom.password.PasswordEncryptionServiceUsingJBcrypt.class,
                org.isisaddons.module.security.dom.permission.PermissionsEvaluationServiceAllowBeatsVeto.class };
    }
}
