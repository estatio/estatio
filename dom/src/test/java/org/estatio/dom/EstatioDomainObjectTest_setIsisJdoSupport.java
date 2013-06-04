package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;


public class EstatioDomainObjectTest_setIsisJdoSupport {
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private IsisJdoSupport mockSupport;
    
    @Test
    public void injected() throws Exception {
        class MyEstatioDomainObject extends EstatioDomainObject {
            public IsisJdoSupport getJdoSupport() {
                return this.isisJdoSupport;
            }
        }
        MyEstatioDomainObject edo = new MyEstatioDomainObject();
        edo.injectIsisJdoSupport(mockSupport);
        assertThat(edo.getJdoSupport(), is(mockSupport));
    }

}
