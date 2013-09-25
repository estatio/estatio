/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.services.settings;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingJdo;

import org.estatio.dom.ApplicationSettingKey;


/**
 * Estatio-specific settings (eg {@link ApplicationSettingKey#epochDate epoch date}.
 * 
 * <p>
 * Delegates to injected {@link ApplicationSettingsServiceForEstatio application settings service}
 * to actually do the persistence.  Also ensures that any {@link ApplicationSettingKey defaults for keys} 
 * have been installed if required.
 */
@Hidden
public class EstatioSettingsService {

    /**
     * @see ApplicationSettingKey#epochDate
     */
    public final static String EPOCH_DATE_KEY = ApplicationSettingKey.epochDate.name();

    /**
     * @see ApplicationSettingKey#epochDate
     */
    @Hidden
    public LocalDate fetchEpochDate() {
        getApplicationSettings().installDefaultsIfRequired();
        final ApplicationSetting epochDate = applicationSettingsService.find(EPOCH_DATE_KEY);
        return epochDate != null ? epochDate.valueAsLocalDate() : null;
    }

    /**
     * @see ApplicationSettingKey#epochDate
     */
    @Hidden
    public void updateEpochDate(
            final LocalDate newEpochDate) {
        getApplicationSettings().installDefaultsIfRequired();
        final ApplicationSettingJdo setting = find(EPOCH_DATE_KEY);
        if(setting!=null) {
            if(newEpochDate != null) {
                setting.updateAsLocalDate(newEpochDate);
            } else {
                setting.delete(true);
            }
        } else {
            if(newEpochDate != null) {
                getApplicationSettings().newLocalDate(EPOCH_DATE_KEY, "Cutover date to Estatio", newEpochDate);
            } else {
                // no-op
            }
        }
    }

    @Hidden
    public List<ApplicationSetting> listAll() {
        return applicationSettingsService.listAll();
    }

    private ApplicationSettingJdo find(final String key) {
        return (ApplicationSettingJdo) getApplicationSettings().find(key);
    }


    // //////////////////////////////////////

    protected ApplicationSettingsServiceForEstatio applicationSettingsService;

    private ApplicationSettingsServiceForEstatio getApplicationSettings() {
        return (ApplicationSettingsServiceForEstatio) applicationSettingsService;
    }

    public final void injectApplicationSettings(final ApplicationSettingsServiceForEstatio applicationSettings) {
        this.applicationSettingsService = applicationSettings;
    }


}
