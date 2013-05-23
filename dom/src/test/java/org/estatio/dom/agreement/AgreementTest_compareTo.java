package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.party.Party;


public class AgreementTest_compareTo {

    private Agreement item1;
    private Agreement item2;
    private Agreement item3;
    private Agreement item4;
    
    
    @Before
    public void setup() {

        item1 = newAgreement();
        item2 = newAgreement();
        item3 = newAgreement();
        item4 = newAgreement();
    }


    @Test
    public void onReference() {
        
        item2.setReference("ABC");
        item3.setReference("ABC");
        item4.setReference("DEF");
        
        assertOrder();
    }


    void assertOrder() {
        assertThat(item1.compareTo(item2), is(Matchers.lessThan(0)));
        assertThat(item2.compareTo(item1), is(Matchers.greaterThan(0)));
        
        assertThat(item2.compareTo(item3), is(0));
        
        assertThat(item3.compareTo(item4), is(Matchers.lessThan(0)));
        assertThat(item4.compareTo(item3), is(Matchers.greaterThan(0)));
    }
    
    private Agreement newAgreement() {
        return new Agreement() {
            
            @Override
            @MemberOrder(sequence = "4")
            public Party getSecondaryParty() {
                return null;
            }
            
            @Override
            @MemberOrder(sequence = "3")
            public Party getPrimaryParty() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

}
