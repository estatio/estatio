package org.estatio.dom;

import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;

public interface ApplicationSettingCreator {
    void create(ApplicationSettingsServiceRW appSettings);
    String name();
}