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
package org.estatio.integration.tests.financial;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.financial.BankMandates;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class BankMandatesTest_finder extends EstatioIntegrationTest {

    private FinancialAccounts financialAccounts;
    
    private BankMandates bankMandates;
    
    
    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    @Before
    public void setUp() throws Exception {
        bankMandates = service(BankMandates.class);
        financialAccounts = service(FinancialAccounts.class);
    }
    
    
    @Test
    public void testFindMandatesFor() {
        FinancialAccount account = financialAccounts.findAccountByReference("NL31ABNA0580744435"); // Associated with TOPMODEL
        List<BankMandate> mandates = bankMandates.findBankMandatesFor((org.estatio.dom.financial.BankAccount) account);
        assertThat(mandates.size(), is(1));
        
    }
    

}
