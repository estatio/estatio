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

import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.TypeOf;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.core.runtime.fixtures.FixturesInstallerDelegate;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.fixture.EstatioFixture;
import org.estatio.fixture.agreement.AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture;
import org.estatio.fixture.currency.CurrenciesFixture;
import org.estatio.fixture.index.IndexAndIndexBaseAndIndexValueFixture;
import org.estatio.fixture.link.LinksFixture;
import org.estatio.fixturescripts.CreateRetroInvoices;
import org.estatio.fixturescripts.FixtureScript;
import org.estatio.services.settings.ApplicationSettingForEstatio;
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

    @TypeOf(ApplicationSettingForEstatio.class)
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
    @MemberOrder(sequence = "4")
    public String installLinksFixture() {
        installFixtures(container.newTransientInstance(LinksFixture.class));
        return "Links fixture successfully installed";
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(sequence = "9")
    public String installConstants() {
        AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture fixture = container.newTransientInstance(AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture.class);
        fixture.install();
        CurrenciesFixture currenciesFixture = container.newTransientInstance(CurrenciesFixture.class);
        currenciesFixture.install();

        invoices.createCollectionNumberNumerator("%08d", BigInteger.ZERO);

        return "Constants successfully installed";
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(sequence = "9")
    public String installCurrencies() {
        CurrenciesFixture currenciesFixture = container.newTransientInstance(CurrenciesFixture.class);
        currenciesFixture.install();
        return "Constants successfully installed";
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "9")
    @Prototype
    public Object runScript(FixtureScript fixtureScript) {
        return fixtureScript.run(container);
    }

    public FixtureScript default0RunScript() {
        return FixtureScript.CREATE_BREAK_OPTIONS;
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "9")
    @Prototype
    public void createRetroInvoicesForAllProperties(
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") @Optional LocalDate nextDueDate) {
        CreateRetroInvoices creator = container.newTransientInstance(CreateRetroInvoices.class);
        creator.createAllProperties(startDueDate, nextDueDate);
    }

    @MemberOrder(sequence = "9")
    @Prototype
    public void createRetroInvoicesForProperty(
            final Property property,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") @Optional LocalDate nextDueDate) {
        CreateRetroInvoices creator = container.newTransientInstance(CreateRetroInvoices.class);
        creator.createProperty(property, startDueDate, nextDueDate);
    }

    @MemberOrder(sequence = "9")
    @Prototype
    public void createRetroInvoicesForLease(
            final Lease lease,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") LocalDate nextDueDate) {
        CreateRetroInvoices creator = container.newTransientInstance(CreateRetroInvoices.class);
        creator.createLease(lease, startDueDate, nextDueDate);
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
