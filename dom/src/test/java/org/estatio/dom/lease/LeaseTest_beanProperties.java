package org.estatio.dom.lease;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
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
	        .withFixture(pojos(BankMandate.class))
	        .exercise(new Lease());
	}


}
