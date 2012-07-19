package com.eurocommercialproperties.estatio.dom.communicationchannel;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class CommunicationChannelTest_beanProperties {

	public static class CommunicationChannelForTesting extends CommunicationChannel {}
	
	@Test
	public void test() {
		new PojoTester().exercise(new CommunicationChannelForTesting(), FilterSet.excluding("container"));
	}

}
