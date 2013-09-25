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
package org.estatio.services.settings;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingsServiceJdoHidden;

import org.estatio.dom.ApplicationSettingCreator;

public class ApplicationSettingsServiceForEstatio extends ApplicationSettingsServiceJdoHidden  {

    @Override
    public ApplicationSetting find(final String key) {
        installDefaultsIfRequired();
        return super.find(key);
    }

    @Override
    public List<ApplicationSetting> listAll() {
        installDefaultsIfRequired();
        return super.listAll();
    }

    
    private boolean installedDefaults;
    /**
     * Not API.
     * 
     * <p>
     * horrid, but cannot use @PostConstruct since no container injected, and no Isis session available
     */
    @Hidden
    public void installDefaultsIfRequired() {
        if(!installedDefaults) {
            installedDefaults = true;
            installDefaults();
        }
    }

    private void installDefaults() {
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
        ApplicationSetting find = find(creator.name());
        if(find == null) {
            creator.create(this);
        }
    }

}

