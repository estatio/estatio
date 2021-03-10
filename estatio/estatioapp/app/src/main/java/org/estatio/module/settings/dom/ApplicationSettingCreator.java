/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.module.settings.dom;

import org.joda.time.LocalDate;

public interface ApplicationSettingCreator {
    void create(ApplicationSettingsServiceRW appSettings);
    ApplicationSetting find(ApplicationSettingsServiceRW appSettings);

    /**
     *
     * @return
     */
    String prefix();

    String name();
    
    Class<?> getDataType();
    String getDescription();
    Object getDefaultValue();

    final static class Helper {
        private Helper(){}
        public static ApplicationSetting find(
                final ApplicationSettingCreator creator,
                final ApplicationSettingsServiceRW appSettings) {
            return appSettings.find(ApplicationSettingCreator.Helper.getKey(creator));
        }
        public static ApplicationSetting findOrCreate(
                final ApplicationSettingCreator creator,
                final ApplicationSettingsServiceRW appSettings) {
            final ApplicationSetting applicationSetting = appSettings.find(Helper.getKey(creator));
            if (applicationSetting == null){
                return create(creator, appSettings);
            }
            return applicationSetting;
        }
        public static ApplicationSetting create(
                final ApplicationSettingCreator creator,
                final ApplicationSettingsServiceRW appSettings) {
            final Class<?> dataType = creator.getDataType();
            final String key = getKey(creator);
            final String description = creator.getDescription();

            if(dataType == LocalDate.class) {
                return appSettings.newLocalDate(key, description, (LocalDate)creator.getDefaultValue());
            }
            if(dataType == Boolean.class) {
                return appSettings.newBoolean(key, description, (Boolean)creator.getDefaultValue());
            }
            if(dataType == Integer.class) {
                return appSettings.newInt(key, description, (Integer)creator.getDefaultValue());
            }
            if(dataType == Long.class) {
                return appSettings.newLong(key, description, (Long)creator.getDefaultValue());
            }
            if(dataType == String.class) {
                return appSettings.newString(key, description, (String)creator.getDefaultValue());
            }
            return null;
        }

        public static String getKey(final ApplicationSettingCreator creator) {
            return creator.prefix()+"."+creator.name();
        }
    }

}