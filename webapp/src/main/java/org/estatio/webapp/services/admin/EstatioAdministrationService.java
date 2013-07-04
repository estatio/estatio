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
package org.estatio.webapp.services.admin;

import java.util.List;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.index.Indices;
import org.estatio.fixture.EstatioFixture;
import org.estatio.fixture.agreement.AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture;
import org.estatio.fixture.index.IndexFixture;
import org.estatio.fixturescripts.FixtureScript;
import org.estatio.services.settings.EstatioSettingsService;
import org.estatio.webapp.services.scheduler.SchedulerServiceForEstatio;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.applib.services.settings.ApplicationSettingsService;

@Named("Administration")
public class EstatioAdministrationService {

    @MemberOrder(sequence = "1")
    public void initializeSchedulerJobs() {
        scheduler.initializeJobs();
    }

    @Prototype
    @MemberOrder(sequence = "2")
    public String installDemoFixtures() {
        EstatioFixture fixtures = container.newTransientInstance(EstatioFixture.class);
        fixtures.install();
        return "Demo fixtures successfully installed";
    }

    public String disableInstallDemoFixtures() {
        return !propertiesService.allProperties().isEmpty() ? "Demo fixtures already installed" : null;
    }

    @Prototype
    @MemberOrder(sequence = "3")
    public String installIndexFixture() {
        IndexFixture fixture = container.newTransientInstance(IndexFixture.class);
        fixture.install();
        return "Index fixture successfully installed";
    }

    public String disableInstallIndexFixture() {
        return !indices.allIndices().isEmpty() ? "Index fixture already installed" : null;
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(sequence = "4")
    public String installConstants() {
        AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture fixture = container.newTransientInstance(AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture.class);
        fixture.install();
        return "Constants successfully installed";
    }

    @Prototype
    @MemberOrder(sequence = "5")
    public void updateEpochDate(@Named("Epoch Date") LocalDate epochDate) {
        settingsService.updateEpochDate(epochDate);
    }

    @MemberOrder(sequence = "9")
    @Prototype
    public void runScript(FixtureScript fixtureScript) {
        fixtureScript.run(container);
    }

    public FixtureScript default0RunScript() {
        return FixtureScript.GenerateTopModelInvoice;
    }

    
    public List<ApplicationSetting> listAllSettings() {
        return applicationSettingsService.listAll();
    }
    
    // //////////////////////////////////////
    
    private DomainObjectContainer container;

    public void setContainer(DomainObjectContainer container) {
        this.container = container;
    }

    private SchedulerServiceForEstatio scheduler;

    public void injectSchedulerService(SchedulerServiceForEstatio scheduler) {
        this.scheduler = scheduler;
    }

    private Indices indices;

    public void injectIndices(Indices indices) {
        this.indices = indices;
    }

    private Properties propertiesService;

    public void injectProperties(Properties propertiesService) {
        this.propertiesService = propertiesService;
    }

    private EstatioSettingsService settingsService;

    public void injectSettingsService(EstatioSettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    private ApplicationSettingsService applicationSettingsService;
    public void injectApplicationSettingsService(ApplicationSettingsService applicationSettingsService) {
        this.applicationSettingsService = applicationSettingsService;
    }

}
