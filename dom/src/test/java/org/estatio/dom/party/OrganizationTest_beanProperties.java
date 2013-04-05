package org.estatio.dom.party;

import org.estatio.dom.party.Organisation;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class OrganizationTest_beanProperties {

	@Test
	public void test() {
		new PojoTester().exercise(new Organisation(), FilterSet.excluding("container", "roles", "communicationChannels", "registrations", "accounts"));
	}

}
