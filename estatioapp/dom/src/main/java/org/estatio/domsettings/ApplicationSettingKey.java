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
package org.estatio.domsettings;

import org.joda.time.LocalDate;

import org.isisaddons.module.settings.dom.ApplicationSetting;
import org.isisaddons.module.settings.dom.ApplicationSettingsServiceRW;


public enum ApplicationSettingKey implements ApplicationSettingCreator {
    
    /**
     * The 'beginning of time' so far as Estatio is concerned.
     * 
     * <p>
     * This is used, for example, by the <tt>InvoiceCalculationService</tt>; it
     * doesn't go looking for invoices prior to this date because they won't
     * exist in the system.
     * 
     * <p>
     * One of the design principles for Estatio was to ensure that it would not
     * require invoices from the predecessor system.
     */
    epochDate(LocalDate.class, "Epoch date", new LocalDate(2013,4,1)),
    reportServerBaseUrl(String.class, "Report server base URL", "http://ams-s-sql08/ReportServer/Pages/ReportViewer.aspx?/Estatio/");
    
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