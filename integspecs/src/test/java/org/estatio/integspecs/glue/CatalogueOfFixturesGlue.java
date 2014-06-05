/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.integspecs.glue;

import cucumber.api.java.Before;

import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.financial.*;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.*;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

/**
 * A catalogue of different fixtures for features to use;
 * select by specifying the appropriate tag.
 */
public class CatalogueOfFixturesGlue extends CukeGlueAbstract {

    @Before({"@integration", "@EstatioTransactionalObjectsFixture"})
    public void beforeScenarioEstatioTransactionalObjectsFixture() {
        scenarioExecution().install(
                new FixtureScript() {
                    @Override
                    protected void execute(ExecutionContext executionContext) {
                        execute(new EstatioBaseLineFixture(), executionContext);

                        // execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsForAll(), executionContext);
                        execute(new OrganisationForAcme(), executionContext);
                        execute(new OrganisationForHelloWorld(), executionContext);
                        execute(new OrganisationForTopModel(), executionContext);
                        execute(new OrganisationForMediaX(), executionContext);
                        execute(new OrganisationForPoison(), executionContext);
                        execute(new OrganisationForPret(), executionContext);
                        execute(new OrganisationForMiracle(), executionContext);
                        execute(new PersonForJohnDoe(), executionContext);
                        execute(new PersonForLinusTorvalds(), executionContext);

                        // execute("properties", new PropertiesAndUnitsForAll(), executionContext);
                        execute(new PropertyForOxf(), executionContext);
                        execute(new PropertyForKal(), executionContext);

                        // execute("leases", new LeasesEtcForAll(), executionContext);
                        execute(new LeaseBreakOptionsForOxfTopModel001(), executionContext);
                        execute(new LeaseBreakOptionsForOxfMediax002(), executionContext);
                        execute(new LeaseBreakOptionsForOxfPoison003(), executionContext);
                        execute(new LeaseForOxfPret004(), executionContext);
                        execute(new LeaseItemAndTermsForOxfMiracl005(), executionContext);
                        execute(new LeaseItemAndLeaseTermForRentForKalPoison001(), executionContext);

                        //execute("invoices", new InvoicesAndInvoiceItemsForAll(), executionContext);
                        execute(new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003(), executionContext);
                        execute(new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001(), executionContext);

                        //execute("bank-accounts", new BankAccountsAndMandatesForAll(), executionContext);
                        execute(new BankAccountForAcme(), executionContext);
                        execute(new BankAccountForHelloWorld(), executionContext);
                        execute(new BankAccountForMediaX(), executionContext);
                        execute(new BankAccountForMiracle(), executionContext);
                        execute(new BankAccountAndMandateForPoison(), executionContext);
                        execute(new BankAccountForPret(), executionContext);
                        execute(new BankAccountAndMandateForTopModel(), executionContext);
                    }
                }
        );
    }
    
    @Before({"@integration", "@LeasesOnlyFixture"})
    public void beforeScenarioLeasesOnlyFixture() {
        scenarioExecution().install(
                new FixtureScript() {
                    @Override
                    protected void execute(ExecutionContext executionContext) {
                        execute(new EstatioBaseLineFixture(), executionContext);

                        // execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsForAll(), executionContext);
                        execute(new OrganisationForAcme(), executionContext);
                        execute(new OrganisationForHelloWorld(), executionContext);
                        execute(new OrganisationForTopModel(), executionContext);
                        execute(new OrganisationForMediaX(), executionContext);
                        execute(new OrganisationForPoison(), executionContext);
                        execute(new OrganisationForPret(), executionContext);
                        execute(new OrganisationForMiracle(), executionContext);
                        execute(new PersonForJohnDoe(), executionContext);
                        execute(new PersonForLinusTorvalds(), executionContext);

                        // execute("properties", new PropertiesAndUnitsForAll(), executionContext);
                        execute(new PropertyForOxf(), executionContext);
                        execute(new PropertyForKal(), executionContext);

                        //execute("leases", new LeaseForAll(), executionContext);
                        execute(new LeaseForOxfTopModel001(), executionContext);
                        execute(new LeaseForOxfMediaX002(), executionContext);
                        execute(new LeaseForOxfPoison003(), executionContext);
                        execute(new LeaseForOxfPret004(), executionContext);
                        execute(new LeaseForOxfMiracl005(), executionContext);
                        execute(new LeaseForKalPoison001(), executionContext);

                        // no lease items or terms
                        // no invoices or invoice items
                    }
                }
        );

    }

}
