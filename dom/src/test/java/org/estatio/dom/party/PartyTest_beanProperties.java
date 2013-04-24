package org.estatio.dom.party;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

import org.junit.Test;

public class PartyTest_beanProperties {

	public static class PartyForTesting extends Person {}
	@Test
	public void test() {
		new PojoTester().exercise(new PartyForTesting(), FilterSet.excluding("container","isisJdoSupport", "communicationChannels", "roles", "registrations", "accounts"));
	}

}
