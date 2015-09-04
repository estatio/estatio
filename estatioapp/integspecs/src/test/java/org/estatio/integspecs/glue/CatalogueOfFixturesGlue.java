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

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.financial.BankAccountForAcmeNl;
import org.estatio.fixture.financial.BankAccountForMediaXGb;
import org.estatio.fixture.financial.BankAccountForMiracleGb;
import org.estatio.fixture.financial.BankAccountForPretGb;
import org.estatio.fixture.financial.BankAccountAndMandateForPoisonNl;
import org.estatio.fixture.financial.BankAccountForHelloWorldGb;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.fixture.lease.LeaseForKalPoison001Nl;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForRentForKalPoison001;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005Gb;
import org.estatio.fixture.lease.LeaseForOxfMediaX002Gb;
import org.estatio.fixture.lease.LeaseForOxfMiracl005Gb;
import org.estatio.fixture.lease.LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseForOxfPret004Gb;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForMediaXGb;
import org.estatio.fixture.party.OrganisationForMiracleGb;
import org.estatio.fixture.party.OrganisationForPoisonNl;
import org.estatio.fixture.party.OrganisationForPretGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;

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
                        execute(new OrganisationForAcmeNl(), executionContext);
                        execute(new OrganisationForHelloWorldGb(), executionContext);
                        execute(new OrganisationForTopModelGb(), executionContext);
                        execute(new OrganisationForMediaXGb(), executionContext);
                        execute(new OrganisationForPoisonNl(), executionContext);
                        execute(new OrganisationForPretGb(), executionContext);
                        execute(new OrganisationForMiracleGb(), executionContext);
                        execute(new PersonForJohnDoeNl(), executionContext);
                        execute(new PersonForLinusTorvaldsNl(), executionContext);

                        // execute("properties", new PropertiesAndUnitsForAll(), executionContext);
                        execute(new PropertyForOxfGb(), executionContext);
                        execute(new PropertyForKalNl(), executionContext);

                        // execute("leases", new LeasesEtcForAll(), executionContext);
                        execute(new LeaseBreakOptionsForOxfTopModel001(), executionContext);
                        execute(new LeaseBreakOptionsForOxfMediax002Gb(), executionContext);
                        execute(new LeaseBreakOptionsForOxfPoison003Gb(), executionContext);
                        execute(new LeaseForOxfPret004Gb(), executionContext);
                        execute(new LeaseItemAndTermsForOxfMiracl005Gb(), executionContext);
                        execute(new LeaseItemAndLeaseTermForRentForKalPoison001(), executionContext);

                        //execute("invoices", new InvoicesAndInvoiceItemsForAll(), executionContext);
                        execute(new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003(), executionContext);
                        execute(new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001(), executionContext);

                        //execute("bank-accounts", new BankAccountsAndMandatesForAll(), executionContext);
                        execute(new BankAccountForAcmeNl(), executionContext);
                        execute(new BankAccountForHelloWorldGb(), executionContext);
                        execute(new BankAccountForMediaXGb(), executionContext);
                        execute(new BankAccountForMiracleGb(), executionContext);
                        execute(new BankAccountAndMandateForPoisonNl(), executionContext);
                        execute(new BankAccountForPretGb(), executionContext);
                        execute(new BankAccountAndMandateForTopModelGb(), executionContext);
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
                        execute(new OrganisationForAcmeNl(), executionContext);
                        execute(new OrganisationForHelloWorldGb(), executionContext);
                        execute(new OrganisationForTopModelGb(), executionContext);
                        execute(new OrganisationForMediaXGb(), executionContext);
                        execute(new OrganisationForPoisonNl(), executionContext);
                        execute(new OrganisationForPretGb(), executionContext);
                        execute(new OrganisationForMiracleGb(), executionContext);
                        execute(new PersonForJohnDoeNl(), executionContext);
                        execute(new PersonForLinusTorvaldsNl(), executionContext);

                        // execute("properties", new PropertiesAndUnitsForAll(), executionContext);
                        execute(new PropertyForOxfGb(), executionContext);
                        execute(new PropertyForKalNl(), executionContext);

                        //execute("leases", new LeaseForAll(), executionContext);
                        execute(new LeaseForOxfTopModel001Gb(), executionContext);
                        execute(new LeaseForOxfMediaX002Gb(), executionContext);
                        execute(new LeaseForOxfPoison003Gb(), executionContext);
                        execute(new LeaseForOxfPret004Gb(), executionContext);
                        execute(new LeaseForOxfMiracl005Gb(), executionContext);
                        execute(new LeaseForKalPoison001Nl(), executionContext);

                        // no lease items or terms
                        // no invoices or invoice items
                    }
                }
        );

    }

}
