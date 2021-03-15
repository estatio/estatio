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
package org.estatio.module.lease.dom;

import org.joda.time.LocalDate;

import org.estatio.module.settings.dom.ApplicationSetting;
import org.estatio.module.settings.dom.ApplicationSettingsServiceRW;

import org.estatio.module.settings.dom.ApplicationSettingCreator;

public enum ApplicationSettingKey implements ApplicationSettingCreator {
    foo(LocalDate.class, "Lease's foo", new LocalDate(2013,4,1)) {
        @Override public String prefix() {
            return "org.estatio.dom.lease";
        }
    };
    
    private final Object defaultValue;
    private final String description;
    private final Class<?> dataType;
    
    private ApplicationSettingKey(final Class<?> dataType, final String description, final Object defaultValue) {
        this.dataType = dataType;
        this.description = description;
        this.defaultValue = defaultValue;
    }
    @Override
    public void create(final ApplicationSettingsServiceRW appSettings) {
        Helper.create(this, appSettings);
    }
    @Override
    public ApplicationSetting find(final ApplicationSettingsServiceRW appSettings) {
        return Helper.find(this, appSettings);
    }
    @Override
    public Class<?> getDataType() {
        return dataType;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

}
