/*
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
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;
import org.apache.isis.applib.services.settings.SettingAbstract;
import org.apache.isis.applib.services.settings.SettingType;

import org.estatio.dom.ApplicationSettingCreator;

/**
 * Estatio-specific implementation of {@link ApplicationSettingsServiceRW} 
 * which also installs defaults on  {@link #init(Map) initialization}, and lets {@link ApplicationSetting setting}s
 * be {@link #find(String) found} and {@link #listAll() retrieved}.
 */
public class ApplicationSettingsServiceForEstatio extends AbstractService implements ApplicationSettingsServiceRW {

    @Programmatic
    @Override
    public ApplicationSetting find(final String key) {
        return firstMatch(
                new QueryDefault<ApplicationSettingForEstatio>(ApplicationSettingForEstatio.class, 
                        "findByKey", 
                        "key", key));
    }

    // //////////////////////////////////////

    @Programmatic
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<ApplicationSetting> listAll() {
        return (List)allMatches(
                new QueryDefault<ApplicationSettingForEstatio>(ApplicationSettingForEstatio.class, 
                        "findAll"));
    }

    // //////////////////////////////////////

    @Programmatic
    @Override
    public ApplicationSetting newString(
            final String key, 
            final String description, 
            final String value) {
        return newSetting(key, description, SettingType.STRING, value);
    }
    @Programmatic
    @Override
    public ApplicationSettingForEstatio newInt(
            final String key, 
            final String description, 
            final Integer value) {
        return newSetting(key, description, SettingType.INT, value.toString());
    }
    @Programmatic
    @Override
    public ApplicationSettingForEstatio newLong(
            final String key, 
            final String description, 
            final Long value) {
        return newSetting(key, description, SettingType.LONG, value.toString());
    }
    @Programmatic
    @Override
    public ApplicationSettingForEstatio newLocalDate(
            final String key, 
            final String description, 
            final LocalDate value) {
        return newSetting(key, description, SettingType.LOCAL_DATE, value.toString(SettingAbstract.DATE_FORMATTER));
    }
    @Programmatic
    @Override
    public ApplicationSettingForEstatio newBoolean(
            final String key, 
            final String description, 
            final Boolean value) {
        return newSetting(key, description, SettingType.BOOLEAN, new Boolean(value != null && value).toString());
    }

    private ApplicationSettingForEstatio newSetting(
            final String key, final String description, final SettingType settingType, final String valueRaw) {
        final ApplicationSettingForEstatio setting = newTransientInstance(ApplicationSettingForEstatio.class);
        setting.setKey(key);
        setting.setDescription(description);
        setting.setValueRaw(valueRaw);
        setting.setType(settingType);
        persist(setting);
        return setting;
    }


    
    @Programmatic
    @PostConstruct
    public void init(final Map<String,String> props) {
        installDefaultsIfRequired();
    }

    private void installDefaultsIfRequired() {
        createSettingsIfRequired(org.estatio.dom.ApplicationSettingKey.values());
        createSettingsIfRequired(org.estatio.dom.lease.ApplicationSettingKey.values());
        createSettingsIfRequired(org.estatio.dom.invoice.ApplicationSettingKey.values());
    }

    private void createSettingsIfRequired(final ApplicationSettingCreator[] values) {
        for(org.estatio.dom.ApplicationSettingCreator creator: values) {
            createIfRequired(creator);
        }
    }

    private void createIfRequired(final ApplicationSettingCreator creator) {
        ApplicationSetting find = creator.find(this);
        if(find == null) {
            creator.create(this);
        }
    }

}

