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
package org.estatio.module.application.seed;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.settings.dom.ApplicationSetting;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.lease.dom.ApplicationSettingKey;
import org.estatio.module.settings.dom.ApplicationSettingCreator;
import org.estatio.module.settings.dom.ApplicationSettingsServiceForEstatio;

/**
 *Installs default settings on  {@link #init(Map) initialization}.
 */
@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "99")
public class ApplicationSettingsSeedService extends UdoDomainService<ApplicationSettingsSeedService> {

    public ApplicationSettingsSeedService() {
        super(ApplicationSettingsSeedService.class);
    }

    @PostConstruct
    public void init(final Map<String,String> props) {
        super.init(props);
        if(System.getProperty("isis.integTest") != null) {
            return;
        }

        installDefaultsIfRequired();
    }

    private void installDefaultsIfRequired() {
        createSettingsIfRequired(ApplicationSettingKey.values());
    }

    private void createSettingsIfRequired(final ApplicationSettingCreator[] values) {
        for(ApplicationSettingCreator creator: values) {
            createIfRequired(creator);
        }
    }

    private void createIfRequired(final ApplicationSettingCreator creator) {
        ApplicationSetting find = creator.find(applicationSettingsServiceForEstatio);
        if(find == null) {
            creator.create(applicationSettingsServiceForEstatio);
        }
    }


    @Inject
    ApplicationSettingsServiceForEstatio applicationSettingsServiceForEstatio;

}

