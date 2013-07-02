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
package org.estatio.services.appsettings;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingJdo;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingsServiceJdo;


@Hidden
public class EstatioSettingsServiceJdo extends EstatioSettingsService {

    public void updateEpochDate(LocalDate newEpochDate) {
        final ApplicationSettingJdo setting = find(EPOCH_DATE_KEY);
        if(setting!=null) {
            if(newEpochDate != null) {
                setting.updateAsLocalDate(newEpochDate);
            } else {
                setting.delete(true);
            }
        } else {
            if(newEpochDate != null) {
                getApplicationSettings().newLocalDate(EPOCH_DATE_KEY, "Cutover date to Estatio", newEpochDate);
            } else {
                // no-op
            }
        }
    }

    private ApplicationSettingJdo find(String key) {
        return (ApplicationSettingJdo) getApplicationSettings().find(key);
    }

    protected ApplicationSettingsServiceJdo getApplicationSettings() {
        return (ApplicationSettingsServiceJdo) applicationSettings;
    }

}
