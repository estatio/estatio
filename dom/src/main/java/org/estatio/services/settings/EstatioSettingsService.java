/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.applib.services.settings.ApplicationSettingsService;

public abstract class EstatioSettingsService {

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
    public final static String EPOCH_DATE_KEY = "epochDate";

    @Hidden
    public LocalDate fetchEpochDate() {
        final ApplicationSetting epochDate = applicationSettings.find(EPOCH_DATE_KEY);
        return epochDate != null ? epochDate.valueAsLocalDate() : null;
    }

    public abstract void updateEpochDate(LocalDate epochDate);
    


    // //////////////////////////////////////

    protected ApplicationSettingsService applicationSettings;

    public void setApplicationSettings(ApplicationSettingsService applicationSettings) {
        this.applicationSettings = applicationSettings;
    }

}
