package org.estatio.dom.communicationchannel;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

import org.junit.Test;

import org.apache.isis.applib.annotation.Title;

public class CommunicationChannelTest_beanProperties {

    public static class CommunicationChannelForTesting extends CommunicationChannel {

        @Override
        @Title
        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }}
	
	@Test
	public void test() {
		new PojoTester().exercise(new CommunicationChannelForTesting(), FilterSet.excluding("container"));
	}

}
