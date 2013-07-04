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

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingsServiceJdo;

import org.estatio.dom.ApplicationSettingCreator;

@Hidden
public class ApplicationSettingsServiceForEstatio extends ApplicationSettingsServiceJdo  {


    @Override
    @Hidden
    public ApplicationSetting find(@Named("Key") String key) {
        installDefaultsIfRequired();
        return super.find(key);
    }

    @Override
    @MemberOrder(sequence = "1")
    public List<ApplicationSetting> listAll() {
        installDefaultsIfRequired();
        return super.listAll();
    }

    
    private boolean installedDefaults;
    private void installDefaultsIfRequired() {
        // horrid, but cannot use @PostConstruct since no container injected, and no Isis session available
        if(!installedDefaults) {
            installedDefaults = true;
            installDefaults();
        }
    }

    // @PostConstruct
    private void installDefaults() {
        createSettingsIfRequired(org.estatio.dom.ApplicationSettingKey.values());
        createSettingsIfRequired(org.estatio.dom.lease.ApplicationSettingKey.values());
        createSettingsIfRequired(org.estatio.dom.invoice.ApplicationSettingKey.values());
    }

    private void createSettingsIfRequired(ApplicationSettingCreator[] values) {
        for(org.estatio.dom.ApplicationSettingCreator sd: values) {
            create(sd);
        }
    }

    private void create(org.estatio.dom.ApplicationSettingCreator sd) {
        ApplicationSetting find = find(sd.name());
        if(find == null) {
            sd.create(this);
        }
    }

}

