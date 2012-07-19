package com.eurocommercialproperties.estatio.dom.communicationchannel;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class FaxNumberTest_beanProperties {

	@Test
	public void test() {
		new PojoTester().exercise(new FaxNumber(), FilterSet.excluding("container"));
	}

}
