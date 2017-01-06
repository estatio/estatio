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
package org.estatio.app.menus.admin;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.settings.dom.ApplicationSetting;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.appsettings.LeaseInvoicingSettingsService;
import org.estatio.domsettings.ApplicationSettingForEstatio;
import org.estatio.domsettings.ApplicationSettingsServiceForEstatio;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY,
        menuOrder = "10.1"
)
public class AdministrationMenu extends UdoDomainService<AdministrationMenu> {

    public AdministrationMenu() {
        super(AdministrationMenu.class);
    }


    //region > updateEpochDate (action)
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(sequence = "1")
    public void updateEpochDate(
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Epoch Date")
            final LocalDate epochDate) {
        settingsService.updateEpochDate(epochDate);
    }

    public LocalDate default0UpdateEpochDate() {
        return settingsService.fetchEpochDate();
    }
    //endregion


    //region > listAllSettings (action)

    @Action(
            semantics = SemanticsOf.SAFE,
            typeOf = ApplicationSettingForEstatio.class
    )
    @MemberOrder(sequence = "2")
    public List<ApplicationSetting> listAllSettings() {
        return applicationSettingsService.listAll();
    }
    //endregion



    //region > injected dependencies

    @Inject
    private LeaseInvoicingSettingsService settingsService;

    @Inject
    ApplicationSettingsServiceForEstatio applicationSettingsService;

    //endregion

        }
