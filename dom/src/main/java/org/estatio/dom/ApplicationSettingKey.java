package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;

public enum ApplicationSettingKey implements ApplicationSettingCreator {
    epochDate(LocalDate.class, "Epoch date", new LocalDate(2013,4,1));
    
    private final Object defaultValue;
    private final String description;
    private final Class<?> dataType;
    
    private ApplicationSettingKey(Class<?> dataType, String description, Object defaultValue) {
        this.dataType = dataType;
        this.description = description;
        this.defaultValue = defaultValue;
    }
    public void create(ApplicationSettingsServiceRW appSettings) {
        Helper.create(this, appSettings);
    }
    public Class<?> getDataType() {
        return dataType;
    }
    public String getDescription() {
        return description;
    }
    public Object getDefaultValue() {
        return defaultValue;
    }
}