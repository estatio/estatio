package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.filter.Filter;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class AgreementRoleTypeTest {

    private AgreementRoleType art;
    private AgreementType at;
    
    @Mock
    private DomainObjectContainer mockContainer;
    
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setUp() throws Exception {
        at = new AgreementType();
        art = new AgreementRoleType();
        art.setAppliesTo(at);
        at.setContainer(mockContainer);
    }
    

    @Test
    @Ignore // hmmm.
    public void test() {
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).allMatches(with(any(Class.class)), with(any(Filter.class)));
            }
        }
        );

        AgreementRoleType.applicableTo(at);
    }
    


}
