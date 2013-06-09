package org.estatio.jdo;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.services.settings.ApplicationSettingsService;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingJdo;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingsServiceJdo;

import org.estatio.services.appsettings.EstatioSettingsService;

@Hidden
public class EstatioSettingsServiceJdo extends EstatioSettingsService {

    public void updateEpochDate(LocalDate newEpochDate) {
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

    private ApplicationSettingJdo find(String key) {
        return (ApplicationSettingJdo) getApplicationSettings().find(key);
    }

    protected ApplicationSettingsServiceJdo getApplicationSettings() {
        return (ApplicationSettingsServiceJdo) applicationSettings;
    }

}
