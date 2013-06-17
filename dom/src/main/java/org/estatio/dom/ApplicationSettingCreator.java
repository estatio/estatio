package org.estatio.dom;


import org.joda.time.LocalDate;

import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;

public interface ApplicationSettingCreator {
    void create(ApplicationSettingsServiceRW appSettings);
    String name();
    
    Class<?> getDataType();
    String getDescription();
    Object getDefaultValue();

    static class Helper {
        private Helper(){}
        public static void create(final ApplicationSettingCreator creator, ApplicationSettingsServiceRW appSettings) {
            final Class<?> dataType = creator.getDataType();
            final String key = getKey(creator);
            final String description = creator.getDescription();
            
            if(dataType == LocalDate.class) {
                appSettings.newLocalDate(key, description, (LocalDate)creator.getDefaultValue());
            }
            if(dataType == Boolean.class) {
                appSettings.newBoolean(key, description, (Boolean)creator.getDefaultValue());
            }
            if(dataType == Integer.class) {
                appSettings.newInt(key, description, (Integer)creator.getDefaultValue());
            }
            if(dataType == Long.class) {
                appSettings.newLong(key, description, (Long)creator.getDefaultValue());
            }
            if(dataType == String.class) {
                appSettings.newString(key, description, (String)creator.getDefaultValue());
            }
        }

        private static String getKey(final ApplicationSettingCreator creator) {
            return creator.getClass().getPackage().getName()+"."+creator.name();
        }
    }
}