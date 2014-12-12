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
package org.estatio.webapp.services.admin;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.settings.dom.ApplicationSetting;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.*;
import org.estatio.dom.EstatioService;
import org.estatio.services.settings.ApplicationSettingForEstatio;
import org.estatio.services.settings.EstatioSettingsService;

@DomainService()
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY,
        menuOrder = "10.1"
)
public class EstatioAdministrationService extends EstatioService<EstatioAdministrationService> {

    public EstatioAdministrationService() {
        super(EstatioAdministrationService.class);
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public void updateEpochDate(
            @Named("Epoch Date") @Optional final LocalDate epochDate) {
        settingsService.updateEpochDate(epochDate);
    }

    public LocalDate default0UpdateEpochDate() {
        return settingsService.fetchEpochDate();
    }

    // //////////////////////////////////////

    @TypeOf(ApplicationSettingForEstatio.class)
    @MemberOrder(sequence = "2")
    public List<ApplicationSetting> listAllSettings() {
        return settingsService.listAll();
    }

    // //////////////////////////////////////

    @Inject
    private EstatioSettingsService settingsService;

}
