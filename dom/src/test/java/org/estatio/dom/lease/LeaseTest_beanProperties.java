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
package org.estatio.dom.lease;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class LeaseTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Agreement.class, AgreementForTesting.class))
	        .withFixture(pojos(Party.class, PartyForTesting.class))
	        .withFixture(pojos(AgreementType.class))
	        .withFixture(pojos(LeaseType.class))
	        .withFixture(pojos(BankMandate.class))
            .withFixture(statii())
	        .exercise(new Lease());
	}


    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<LeaseStatus> statii() {
        return new FixtureDatumFactory(LeaseStatus.class, (Object[])LeaseStatus.values());
    }

}
