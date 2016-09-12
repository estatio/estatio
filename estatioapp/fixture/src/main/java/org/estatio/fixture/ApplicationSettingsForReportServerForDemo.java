package org.estatio.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.settings.dom.ApplicationSettingsService;

import org.estatio.domsettings.ApplicationSettingForEstatio;

public class ApplicationSettingsForReportServerForDemo extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        final ApplicationSettingForEstatio appSetting = (ApplicationSettingForEstatio) applicationSettingsService
                .find("org.estatio.domsettings.reportServerBaseUrl");

        appSetting.setValueRaw("http://www.pdfpdf.com/samples/Sample5.PDF?name=");
    }

    @Inject
    ApplicationSettingsService applicationSettingsService;
}
