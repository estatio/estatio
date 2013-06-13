package org.estatio.dom.invoice;

import org.estatio.dom.ApplicationSettingCreator;
import org.joda.time.LocalDate;

import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;

public enum ApplicationSettingKey implements ApplicationSettingCreator {
    foo(LocalDate.class, "Invoice's foo", new LocalDate(2013,4,1));
    
    private final Object defaultValue;
    private final String description;
    private final Class<?> dataType;
    
    private ApplicationSettingKey(Class<?> dataType, String description, Object defaultValue) {
        this.dataType = dataType;
        this.description = description;
        this.defaultValue = defaultValue;
    }
    public void create(ApplicationSettingsServiceRW appSettings) {
        if(dataType == LocalDate.class) {
            appSettings.newLocalDate(this.getClass().getPackage().getName()+"."+name(), description, (LocalDate)defaultValue);
        }
    }
}
