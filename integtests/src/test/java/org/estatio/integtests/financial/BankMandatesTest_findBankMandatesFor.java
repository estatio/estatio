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
package org.estatio.integtests.financial;

import java.util.List;
import javax.inject.Inject;
import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.financial.BankMandates;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsForKal;
import org.estatio.fixture.asset.PropertiesAndUnitsForOxf;
import org.estatio.fixture.financial.*;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemForKalPoison001;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemForOxfPoison003;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BankMandatesTest_findBankMandatesFor extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new PersonForJohnDoe(), executionContext);
                execute(new PersonForLinusTorvalds(), executionContext);


                execute(new OrganisationAndCommunicationChannelsForHelloWorld(), executionContext);
                execute(new PropertiesAndUnitsForOxf(), executionContext);
                execute(new BankAccountAndMandateForHelloWorld(), executionContext);

                execute(new OrganisationAndCommunicationChannelsForAcme(), executionContext);
                execute(new PropertiesAndUnitsForKal(), executionContext);
                execute(new BankAccountAndMandateForAcme(), executionContext);


                execute(new OrganisationAndCommunicationChannelsForTopModel(), executionContext);
                execute(new LeasesEtcForOxfTopModel001(), executionContext);
                execute(new BankAccountAndMandateForTopModel(), executionContext);

                execute(new OrganisationAndCommunicationChannelsForMediaX(), executionContext);
                execute(new LeasesEtcForOxfMediax002(), executionContext);
                execute(new BankAccountAndMandateForMediaX(), executionContext);

                execute(new OrganisationAndCommunicationChannelsForPoison(), executionContext);
                execute(new LeasesEtcForOxfPoison003(), executionContext);
                execute(new LeasesEtcForKalPoison001(), executionContext);
                execute(new BankAccountAndMandateForPoison(), executionContext);
                execute(new InvoiceAndInvoiceItemForOxfPoison003(), executionContext);
                execute(new InvoiceAndInvoiceItemForKalPoison001(), executionContext);

                execute(new OrganisationAndCommunicationChannelsForPret(), executionContext);
                execute(new LeasesEtcForOxfPret004(), executionContext);
                execute(new BankAccountAndMandateForPret(), executionContext);

                execute(new OrganisationAndCommunicationChannelsForMiracle(), executionContext);
                execute(new LeasesEtcForOxfMiracl005(), executionContext);
                execute(new BankAccountAndMandateForMiracle(), executionContext);
            }
        });
    }

    @Test
    public void forAccountWithMandate() {
        // given
        FinancialAccount account = financialAccounts.findAccountByReference("NL31ABNA0580744435"); // Associated with TOPMODEL
        // when
        List<BankMandate> mandates = bankMandates.findBankMandatesFor((org.estatio.dom.financial.BankAccount) account);
        // then
        assertThat(mandates.size(), is(1));
    }


    @Inject
    private FinancialAccounts financialAccounts;
    @Inject
    private BankMandates bankMandates;

}
