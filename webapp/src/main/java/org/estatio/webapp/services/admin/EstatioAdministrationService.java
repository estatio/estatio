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

import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.core.runtime.fixtures.FixturesInstallerDelegate;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.Invoices;
import org.estatio.fixture.EstatioFixture;
import org.estatio.fixture.agreement.AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture;
import org.estatio.fixture.index.IndexAndIndexBaseAndIndexValueFixture;
import org.estatio.fixturescripts.FixtureScript;
import org.estatio.services.settings.EstatioSettingsService;

@Named("Administration")
public class EstatioAdministrationService {

    @MemberOrder(sequence = "aaa.1")
    public void updateEpochDate(
            final @Named("Epoch Date") @Optional LocalDate epochDate) {
        settingsService.updateEpochDate(epochDate);
    }
    public LocalDate default0UpdateEpochDate() {
        return settingsService.fetchEpochDate();
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "aaa.2")
    public List<ApplicationSetting> listAllSettings() {
        return settingsService.listAll();
    }

    
    // //////////////////////////////////////

    @Prototype
    @MemberOrder(sequence = "3")
    public String installDemoFixtures() {
        installFixtures(container.newTransientInstance(EstatioFixture.class));
        return "Demo fixtures successfully installed";
    }

    public String disableInstallDemoFixtures() {
        return !propertiesService.allProperties().isEmpty() ? "Demo fixtures already installed" : null;
    }
    
    // //////////////////////////////////////

    @Prototype
    @MemberOrder(sequence = "4")
    public String installIndexFixture() {
        installFixtures(container.newTransientInstance(IndexAndIndexBaseAndIndexValueFixture.class));
        return "Index fixture successfully installed";
    }

    public String disableInstallIndexFixture() {
        return !indices.allIndices().isEmpty() ? "Index fixture already installed" : null;
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(sequence = "5")
    public String installConstants() {
        AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture fixture = container.newTransientInstance(AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture.class);
        fixture.install();

        invoices.createCollectionNumberNumerator("%09d", BigInteger.ZERO);

        return "Constants successfully installed";
    }

    // //////////////////////////////////////


    @MemberOrder(sequence = "9")
    @Prototype
    public void runScript(FixtureScript fixtureScript) {
        fixtureScript.run(container);
    }

    public FixtureScript default0RunScript() {
        return FixtureScript.GenerateTopModelInvoice;
    }


    // //////////////////////////////////////

    private static void installFixtures(final Object fixture) {
        final FixturesInstallerDelegate installer = new FixturesInstallerDelegate().withOverride();
        installer.addFixture(fixture);
        installer.installFixtures();
    }

    // //////////////////////////////////////

    private DomainObjectContainer container;

    public void setContainer(DomainObjectContainer container) {
        this.container = container;
    }

    private Indices indices;

    public final void injectIndices(Indices indices) {
        this.indices = indices;
    }

    private Properties propertiesService;

    public final void injectProperties(Properties propertiesService) {
        this.propertiesService = propertiesService;
    }

    private EstatioSettingsService settingsService;

    public final void injectSettingsService(EstatioSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    private Invoices invoices;
    
    public void injectInvoices(Invoices invoices) {
        this.invoices = invoices;
    }

}
