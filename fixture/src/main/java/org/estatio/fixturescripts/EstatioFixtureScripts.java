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
package org.estatio.fixturescripts;

import java.math.BigInteger;
import java.util.List;
import javax.inject.Inject;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.fixture.EstatioDemoFixture;
import org.estatio.fixture.agreement.AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture;
import org.estatio.fixture.currency.CurrenciesFixture;
import org.estatio.fixture.index.IndexAndIndexBaseAndIndexValueFixture;
import org.estatio.fixture.link.LinksFixture;
import org.estatio.services.settings.EstatioSettingsService;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.fixturescripts.*;

public class EstatioFixtureScripts extends FixtureScripts{

    public EstatioFixtureScripts() {
        super("org.estatio");
    }

    @Paged(50)
    @MemberOrder(name="Administration", sequence = "9")
    @Override
    public List<FixtureResult> runFixtureScript(
            final FixtureScript fixtureScript,
            final String parameters) {
        return super.runFixtureScript(fixtureScript, parameters);
    }

    // //////////////////////////////////////

    @MemberOrder(name="Administration", sequence = "9.1")
    @Prototype
    public List<FixtureResult> createRetroInvoicesForAllProperties(
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") @Optional LocalDate nextDueDate) {
        CreateRetroInvoices creator = getContainer().newTransientInstance(CreateRetroInvoices.class);
        final FixtureScript.ExecutionContext executionContext = newExecutionContext(null);
        creator.createAllProperties(startDueDate, nextDueDate, executionContext);
        return executionContext.getResults();
    }

    @MemberOrder(name="Administration", sequence = "9.2")
    @Prototype
    public List<FixtureResult> createRetroInvoicesForProperty(
            final Property property,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") @Optional LocalDate nextDueDate) {
        final CreateRetroInvoices creator = getContainer().newTransientInstance(CreateRetroInvoices.class);
        final FixtureScript.ExecutionContext executionContext = newExecutionContext(null);
        creator.createProperty(property, startDueDate, nextDueDate, executionContext);
        return executionContext.getResults();
    }

    @MemberOrder(name="Administration", sequence = "9.3")
    @CssClass("danger") // application.css
    public List<FixtureResult> createRetroInvoicesForLease(
            final Lease lease,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") LocalDate nextDueDate) {
        final CreateRetroInvoices creator = getContainer().newTransientInstance(CreateRetroInvoices.class);
        final FixtureScript.ExecutionContext executionContext = newExecutionContext(null);
        creator.createLease(lease, startDueDate, nextDueDate, executionContext);
        return executionContext.getResults();
    }
    public boolean hideCreateRetroInvoicesForLease() {
        return !getContainer().getUser().hasRole("superuser_role");
    }


    // //////////////////////////////////////

    @Prototype
    @MemberOrder(name="Administration", sequence = "3")
    public List<FixtureResult> installDemoFixtures() {
        return runFixtureScript(new EstatioDemoFixture(), null);
        //return "Demo fixtures successfully installed";
    }

    public String disableInstallDemoFixtures() {
        return !propertiesService.allProperties().isEmpty() ? "Demo fixtures already installed" : null;
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(name="Administration", sequence = "4")
    public List<FixtureResult> installIndexFixture() {
        return runFixtureScript(new IndexAndIndexBaseAndIndexValueFixture(), null);
        //return "Index fixture successfully installed";
    }

    public String disableInstallIndexFixture() {
        return !indices.allIndices().isEmpty() ? "Index fixture already installed" : null;
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(name="Administration", sequence = "4")
    public List<FixtureResult> installLinksFixture() {
        return runFixtureScript(new LinksFixture(), null);
        //return "Links fixture successfully installed";
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(name="Administration", sequence = "9")
    public List<FixtureResult> installConstants() {
        return runFixtureScript(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture(), executionContext);
                execute(new CurrenciesFixture(), executionContext);
                execute(new SimpleFixtureScript() {
                    @Override
                    protected void execute(ExecutionContext executionContext) {
                        invoices.createCollectionNumberNumerator("%08d", BigInteger.ZERO);
                    }
                }, executionContext);
            }
        }, null);
        //return "Constants successfully installed";
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(name="Administration", sequence = "9")
    public List<FixtureResult> installCurrencies() {
        return runFixtureScript(new CurrenciesFixture(), null);
        //return "Constants successfully installed";
    }


    // //////////////////////////////////////

    @Inject
    private Indices indices;

    @Inject
    private Properties propertiesService;

    @Inject
    private EstatioSettingsService settingsService;

    @Inject
    private Invoices invoices;

}
