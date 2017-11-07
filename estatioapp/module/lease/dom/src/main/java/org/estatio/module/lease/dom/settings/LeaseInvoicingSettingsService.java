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
package org.estatio.module.lease.dom.settings;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.settings.dom.ApplicationSetting;

import org.estatio.dom.UdoDomainService;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.settings.dom.ApplicationSettingCreator;
import org.estatio.module.settings.dom.ApplicationSettingForEstatio;
import org.estatio.module.settings.dom.ApplicationSettingsServiceForEstatio;

/**
 * Estatio-specific settings (eg {@link LeaseInvoicingSettingKey#epochDate epoch
 * date}.
 * <p/>
 * <p/>
 * Delegates to injected {@link ApplicationSettingsServiceForEstatio application
 * settings service} to actually do the persistence. Also ensures that any
 * {@link LeaseInvoicingSettingKey defaults for keys} have been installed if
 * required.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class LeaseInvoicingSettingsService extends UdoDomainService<LeaseInvoicingSettingsService> {

    public LeaseInvoicingSettingsService() {
        super(LeaseInvoicingSettingsService.class);
    }


    // //////////////////////////////////////


    @Programmatic
    public Currency systemCurrency() {
        //TODO: Make system default currency configurable
        return currencyRepository.findCurrency("EUR");
    }


    // //////////////////////////////////////

    private LocalDate cachedEpochDate;

    /**
     * @see LeaseInvoicingSettingKey#epochDate
     */
    @Programmatic
    public LocalDate fetchEpochDate() {
        if (cachedEpochDate == null) {
            // getApplicationSettings().installDefaultsIfRequired();
            final ApplicationSetting epochDate = applicationSettingsService.find(LeaseInvoicingSettingKey.epochDate);
            if (epochDate != null) {
                cachedEpochDate = epochDate.valueAsLocalDate();
            }
        }
        return cachedEpochDate;
    }

    /**
     * @see LeaseInvoicingSettingKey#epochDate
     */
    @Programmatic
    public void updateEpochDate(
            final LocalDate newEpochDate) {
        // getApplicationSettings().installDefaultsIfRequired();
        final ApplicationSettingForEstatio setting = (ApplicationSettingForEstatio) applicationSettingsService.find(
                LeaseInvoicingSettingKey.epochDate);
        if (setting != null) {
            if (newEpochDate != null) {
                setting.updateAsLocalDate(newEpochDate);
            } else {
                setting.delete();
            }
        } else {
            if (newEpochDate != null) {
                applicationSettingsService.newLocalDate(ApplicationSettingCreator.Helper.getKey(
                        LeaseInvoicingSettingKey.epochDate), "Cutover date to Estatio", newEpochDate);
            } // else no-op
        }
        cachedEpochDate = null;
    }


    // //////////////////////////////////////

    @Inject
    ApplicationSettingsServiceForEstatio applicationSettingsService;

    @Inject
    CurrencyRepository currencyRepository;

}
