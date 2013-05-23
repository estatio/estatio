package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;


public class AgreementRoleTypeTest_compareTo {

    private AgreementRoleType item1;
    private AgreementRoleType item2;
    private AgreementRoleType item3;
    private AgreementRoleType item4;
    
    
    @Before
    public void setup() {

        item1 = newAgreementRoleType();
        item2 = newAgreementRoleType();
        item3 = newAgreementRoleType();
        item4 = newAgreementRoleType();
    }


    @Test
    public void onTitle() {
        
        item2.setTitle("abc");
        item3.setTitle("abc");
        item4.setTitle("def");
        
        assertOrder();
    }

    
    void assertOrder() {
        assertThat(item1.compareTo(item2), is(Matchers.lessThan(0)));
        assertThat(item2.compareTo(item1), is(Matchers.greaterThan(0)));
        
        assertThat(item2.compareTo(item3), is(0));
        
        assertThat(item3.compareTo(item4), is(Matchers.lessThan(0)));
        assertThat(item4.compareTo(item3), is(Matchers.greaterThan(0)));
    }
    
    private AgreementRoleType newAgreementRoleType() {
        return new AgreementRoleType();
    }

}
