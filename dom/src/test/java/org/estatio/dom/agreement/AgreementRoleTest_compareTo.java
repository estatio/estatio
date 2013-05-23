package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.party.Party;


public class AgreementRoleTest_compareTo {

    private AgreementRole item1;
    private AgreementRole item2;
    private AgreementRole item3;
    private AgreementRole item4;
    
    private AgreementRoleType type1;
    private AgreementRoleType type2;
    
    
    @Before
    public void setup() {

        type1 = new AgreementRoleType();
        type2 = new AgreementRoleType();
        type1.setTitle("Abc");
        type2.setTitle("Def");
        
        item1 = newAgreementRole();
        item2 = newAgreementRole();
        item3 = newAgreementRole();
        item4 = newAgreementRole();
    }


    @Test
    public void onType() {
        
        item2.setType(type1);
        item3.setType(type1);
        item4.setType(type2);
        
        assertOrder();
    }
    
    @Test
    public void onStartDate() {
        
        item1.setType(type1);
        item2.setType(type1);
        item3.setType(type1);
        item4.setType(type1);
        
        item2.setStartDate(new LocalDate(2013,4,1));
        item3.setStartDate(new LocalDate(2013,4,1));
        item4.setStartDate(new LocalDate(2013,5,2));
        
        assertOrder();
    }

    void assertOrder() {
        assertThat(item1.compareTo(item2), is(Matchers.lessThan(0)));
        assertThat(item2.compareTo(item1), is(Matchers.greaterThan(0)));
        
        assertThat(item2.compareTo(item3), is(0));
        
        assertThat(item3.compareTo(item4), is(Matchers.lessThan(0)));
        assertThat(item4.compareTo(item3), is(Matchers.greaterThan(0)));
    }
    
    private AgreementRole newAgreementRole() {
        return new AgreementRole();
    }

}
