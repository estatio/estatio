/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.appsettings;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.settings.dom.ApplicationSetting;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.currency.CurrencyRepository;
import org.estatio.domsettings.ApplicationSettingCreator;
import org.estatio.domsettings.ApplicationSettingForEstatio;
import org.estatio.domsettings.ApplicationSettingsServiceForEstatio;

/**
 * Estatio-specific settings (eg {@link ApplicationSettingKey#epochDate epoch
 * date}.
 * <p/>
 * <p/>
 * Delegates to injected {@link ApplicationSettingsServiceForEstatio application
 * settings service} to actually do the persistence. Also ensures that any
 * {@link ApplicationSettingKey defaults for keys} have been installed if
 * required.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class EstatioSettingsService extends UdoDomainService<EstatioSettingsService> {

    public EstatioSettingsService() {
        super(EstatioSettingsService.class);
    }

    public final static String REPORT_SERVER_CONFIG_PROPERTY_KEY = "estatio.application.reportServerBaseUrl";
    public final static String REPORT_SERVER_CONFIG_PROPERTY_DEFAULT = "http://www.pdfpdf.com/samples/Sample5.PDF?name=";

    // //////////////////////////////////////


    @PostConstruct
    public void init(final Map<String,String> properties) {
        String reportServerUrl = properties.get(REPORT_SERVER_CONFIG_PROPERTY_KEY);
        if(reportServerUrl == null) {
            reportServerUrl = REPORT_SERVER_CONFIG_PROPERTY_DEFAULT;
        }
        this.cachedReportServerBaseUrl = reportServerUrl;
    }

    @Programmatic
    public Currency systemCurrency() {
        //TODO: Make system default currency configurable
        return currencyRepository.findCurrency("EUR");
    }

    // //////////////////////////////////////

    private LocalDate cachedEpochDate;

    /**
     * @see ApplicationSettingKey#epochDate
     */
    @Programmatic
    public LocalDate fetchEpochDate() {
        if (cachedEpochDate == null) {
            // getApplicationSettings().installDefaultsIfRequired();
            final ApplicationSetting epochDate = applicationSettingsService.find(ApplicationSettingKey.epochDate);
            if (epochDate != null) {
                cachedEpochDate = epochDate.valueAsLocalDate();
            }
        }
        return cachedEpochDate;
    }

    /**
     * @see ApplicationSettingKey#epochDate
     */
    @Programmatic
    public void updateEpochDate(
            final LocalDate newEpochDate) {
        // getApplicationSettings().installDefaultsIfRequired();
        final ApplicationSettingForEstatio setting = (ApplicationSettingForEstatio) applicationSettingsService.find(ApplicationSettingKey.epochDate);
        if (setting != null) {
            if (newEpochDate != null) {
                setting.updateAsLocalDate(newEpochDate);
            } else {
                setting.delete();
            }
        } else {
            if (newEpochDate != null) {
                applicationSettingsService.newLocalDate(ApplicationSettingCreator.Helper.getKey(ApplicationSettingKey.epochDate), "Cutover date to Estatio", newEpochDate);
            } // else no-op
        }
        cachedEpochDate = null;
    }

    // //////////////////////////////////////

    private String cachedReportServerBaseUrl;

    @Programmatic
    public String fetchReportServerBaseUrl() {
        return cachedReportServerBaseUrl;
    }

    // //////////////////////////////////////

    @Inject
    ApplicationSettingsServiceForEstatio applicationSettingsService;

    @Inject
    CurrencyRepository currencyRepository;

}
