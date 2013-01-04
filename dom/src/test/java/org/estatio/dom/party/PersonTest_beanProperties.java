package org.estatio.dom.party;

import org.estatio.dom.party.Person;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class PersonTest_beanProperties {

	@Test
	public void test() {
		new PojoTester().exercise(new Person(), FilterSet.excluding("container"));
	}

}
