package org.estatio.dom.communicationchannel;

import org.estatio.dom.communicationchannel.EmailAddress;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class EmailAddressTest_beanProperties {

	@Test
	public void test() {
		new PojoTester().exercise(new EmailAddress(), FilterSet.excluding("container"));
	}

}
