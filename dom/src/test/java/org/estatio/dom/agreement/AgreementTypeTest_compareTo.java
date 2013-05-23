package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;


public class AgreementTypeTest_compareTo {

    private AgreementType item1;
    private AgreementType item2;
    private AgreementType item3;
    private AgreementType item4;
    
    
    @Before
    public void setup() {

        item1 = newAgreementType();
        item2 = newAgreementType();
        item3 = newAgreementType();
        item4 = newAgreementType();
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
    
    private AgreementType newAgreementType() {
        return new AgreementType();
    }

}
