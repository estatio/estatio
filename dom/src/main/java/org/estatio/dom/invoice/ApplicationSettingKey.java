/*
 *
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
package org.estatio.dom.invoice;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;

import org.estatio.dom.ApplicationSettingCreator;

public enum ApplicationSettingKey implements ApplicationSettingCreator {
    foo(LocalDate.class, "Invoice's foo", new LocalDate(2013,4,1));
    
    private final Object defaultValue;
    private final String description;
    private final Class<?> dataType;
    
    private ApplicationSettingKey(final Class<?> dataType, final String description, final Object defaultValue) {
        this.dataType = dataType;
        this.description = description;
        this.defaultValue = defaultValue;
    }
    public void create(final ApplicationSettingsServiceRW appSettings) {
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
