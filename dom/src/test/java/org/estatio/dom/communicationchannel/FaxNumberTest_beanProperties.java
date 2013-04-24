package org.estatio.dom.communicationchannel;

import org.estatio.dom.communicationchannel.FaxNumber;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class FaxNumberTest_beanProperties {

	@Test
	public void test() {
		new PojoTester().exercise(new FaxNumber(), FilterSet.excluding("container", "isisJdoSupport"));
	}

}
