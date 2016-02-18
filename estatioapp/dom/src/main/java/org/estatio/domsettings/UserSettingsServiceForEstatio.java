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
package org.estatio.domsettings;

import java.util.List;
import org.isisaddons.module.settings.dom.SettingAbstract;
import org.isisaddons.module.settings.dom.SettingType;
import org.isisaddons.module.settings.dom.UserSetting;
import org.isisaddons.module.settings.dom.UserSettingsServiceRW;
import org.joda.time.LocalDate;
import org.apache.isis.applib.AbstractService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;

/**
 * An implementation of {@link org.isisaddons.module.settings.dom.UserSettingsService} that
 * persists settings as entities into a JDO-backed database.
 */
public class UserSettingsServiceForEstatio extends AbstractService implements UserSettingsServiceRW {

    @Programmatic
    @Override
    public UserSetting find(
            final String user, 
            final String key) {
        return firstMatch(
                new QueryDefault<>(UserSettingForEstatio.class,
                        "findByUserAndKey",
                        "user", user,
                        "key", key));
    }


    // //////////////////////////////////////

    @Programmatic
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<UserSetting> listAllFor(final String user) {
        return (List)allMatches(
                new QueryDefault<>(UserSettingForEstatio.class,
                        "findByUser",
                        "user", user));
    }

    // //////////////////////////////////////

    @Programmatic
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<UserSetting> listAll() {
        return (List)allMatches(
                new QueryDefault<>(UserSettingForEstatio.class,
                        "findAll"));
    }


    // //////////////////////////////////////
    
    @Programmatic
    @Override
    public UserSettingForEstatio newString(
            final String user, 
            final String key, 
            final String description, 
            final String value) {
        return newSetting(user, key, description, SettingType.STRING, value);
    }

    @Programmatic
    @Override
    public UserSettingForEstatio newInt(
            final String user, 
            final String key, 
            final String description, 
            final Integer value) {
        return newSetting(user, key, description, SettingType.INT, value.toString());
    }

    @Programmatic
    @Override
    public UserSettingForEstatio newLong(
            final String user, 
            final String key, 
            final String description, 
            final Long value) {
        return newSetting(user, key, description, SettingType.LONG, value.toString());
    }

    @Programmatic
    @Override
    public UserSettingForEstatio newLocalDate(
            final String user, 
            final String key, 
            final String description, 
            final LocalDate value) {
        return newSetting(user, key, description, SettingType.LOCAL_DATE, 
                value.toString(SettingAbstract.DATE_FORMATTER));
    }

    @Programmatic
    @Override
    public UserSettingForEstatio newBoolean(
            final String user, 
            final String key, 
            final String description, 
            final Boolean value) {
        return newSetting(user, key, description, SettingType.BOOLEAN, new Boolean(value != null && value).toString());
    }

    private UserSettingForEstatio newSetting(
            final String user, final String key, final String description, 
            final SettingType settingType, final String valueRaw) {
        final UserSettingForEstatio setting = newTransientInstance(UserSettingForEstatio.class);
        setting.setUser(user);
        setting.setKey(key);
        setting.setType(settingType);
        setting.setDescription(description);
        setting.setValueRaw(valueRaw);
        persist(setting);
        return setting;
    }

}
