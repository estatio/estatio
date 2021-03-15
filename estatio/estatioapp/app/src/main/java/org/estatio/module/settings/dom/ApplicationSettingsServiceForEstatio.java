/*
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
package org.estatio.module.settings.dom;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

/**
 * Estatio-specific implementation of {@link ApplicationSettingsServiceRW} 
 * which also installs defaults on  {@link #init(Map) initialization}, and lets {@link ApplicationSetting setting}s
 * be {@link #find(String) found} and {@link #listAll() retrieved}.
 */
@DomainService(menuOrder = "99", repositoryFor = ApplicationSettingForEstatio.class)
public class ApplicationSettingsServiceForEstatio extends UdoDomainRepositoryAndFactory<ApplicationSettingForEstatio> implements ApplicationSettingsServiceRW {

    public ApplicationSettingsServiceForEstatio() {
        super(ApplicationSettingsServiceForEstatio.class, ApplicationSettingForEstatio.class);
    }

    @Programmatic
    public ApplicationSetting find(final ApplicationSettingCreator creator) {
        return find(ApplicationSettingCreator.Helper.getKey(creator));
    }

    @Programmatic
    @Override
    public ApplicationSetting find(final String key) {
        return queryResultsCache.execute(
                () -> repositoryService.firstMatch(
                        new QueryDefault<>(ApplicationSettingForEstatio.class,
                            "findByKey",
                            "key", key)
                ), getClass(), "find", key);
    }

    @Inject
    QueryResultsCache queryResultsCache;

    // //////////////////////////////////////

    @Programmatic
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<ApplicationSetting> listAll() {
        return (List)repositoryService.allMatches(
                new QueryDefault<>(ApplicationSettingForEstatio.class,
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

    @Override
    public void delete(final ApplicationSetting applicationSetting) {
        repositoryService.removeAndFlush(applicationSetting);
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
        final ApplicationSettingForEstatio setting = factoryService.instantiate(ApplicationSettingForEstatio.class);
        setting.setKey(key);
        setting.setDescription(description);
        setting.setValueRaw(valueRaw);
        setting.setType(settingType);
        repositoryService.persist(setting);
        return setting;
    }

    @Programmatic
    @PostConstruct
    public void init(final Map<String,String> props) {
        super.init(props);
    }

    @Inject
    RepositoryService repositoryService;


}

