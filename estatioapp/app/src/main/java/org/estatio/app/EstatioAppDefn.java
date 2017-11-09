package org.estatio.app;

import org.incode.module.base.services.calendar.CalendarService;

import org.estatio.dom.dto.EstatioBaseDtoModule;
import org.estatio.fixture.EstatioFixtureModule;
import org.estatio.fixturescripts.EstatioFixtureScriptsModule;
import org.estatio.module.agreement.EstatioAgreementModule;
import org.estatio.module.application.EstatioApplicationModule;
import org.estatio.module.asset.EstatioAssetModule;
import org.estatio.module.assetfinancial.EstatioAssetFinancialModule;
import org.estatio.module.bankaccount.EstatioBankAccountModule;
import org.estatio.module.bankmandate.EstatioBankMandateModule;
import org.estatio.module.budget.EstatioBudgetingModule;
import org.estatio.module.budgetassignment.EstatioBudgetAssignmentModule;
import org.estatio.module.capex.EstatioCapexModule;
import org.estatio.module.charge.EstatioChargeModule;
import org.estatio.module.country.EstatioCountryModule;
import org.estatio.module.currency.EstatioCurrencyModule;
import org.estatio.module.event.EstatioEventModule;
import org.estatio.module.financial.EstatioFinancialModule;
import org.estatio.module.guarantee.EstatioGuaranteeModule;
import org.estatio.module.index.EstatioIndexModule;
import org.estatio.module.invoice.EstatioInvoiceModule;
import org.estatio.module.lease.EstatioLeaseModule;
import org.estatio.module.link.EstatioLinkModule;
import org.estatio.module.numerator.EstatioNumeratorModule;
import org.estatio.module.party.EstatioPartyModule;
import org.estatio.module.registration.EstatioRegistrationModule;
import org.estatio.module.settings.EstatioSettingsModule;
import org.estatio.module.tax.EstatioTaxModule;

class EstatioAppDefn  {

    private EstatioAppDefn(){}

    static Class<?>[] domModulesAndSecurityAndCommandAddon() {
        return new Class<?>[] {

                // the domain modules.  At the moment these aren't actually required to be registered because we also register EstatioDomainModule (above); but this will change when we sort out the package names for these.
                EstatioApplicationModule.class,

                EstatioAgreementModule.class,
                EstatioAssetModule.class,
                EstatioAssetFinancialModule.class,
                EstatioBankMandateModule.class,
                EstatioBudgetingModule.class,
                EstatioBudgetAssignmentModule.class,
                EstatioChargeModule.class,
                EstatioCapexModule.class,
                EstatioCurrencyModule.class,
                EstatioEventModule.class,
                EstatioBankAccountModule.class,
                EstatioFinancialModule.class,
                EstatioGuaranteeModule.class,
                EstatioIndexModule.class,
                EstatioInvoiceModule.class,
                EstatioLeaseModule.class,
                EstatioLinkModule.class,
                EstatioNumeratorModule.class,
                EstatioCountryModule.class,
                EstatioPartyModule.class,
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
