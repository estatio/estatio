package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AgreementTest_roles {

    private Agreement agreement1;
    private Agreement agreement2;
    
    private AgreementRole agreementRole1;
    private AgreementRole agreementRole2;
    
    @Before
    public void setUp() throws Exception {
        agreement1 = new AgreementForTesting();
        agreement2 = new AgreementForTesting();

        agreementRole1 = new AgreementRole();
        agreementRole2 = new AgreementRole();
        
        agreementRole1.setStartDate(new LocalDate(2012,1,1));
        agreementRole2.setStartDate(new LocalDate(2012,2,1));
    }
    
    @Ignore
    @Test
    public void addAtParent() {
        agreement1.addToRoles(agreementRole1);
        
        assertThat(agreement1.getRoles(), Matchers.contains(agreementRole1));
        assertThat(agreementRole1.getAgreement(), is(agreement1));
    }

    @Ignore
    @Test
    public void addAtParentWhenAlreadyChild() {
        // given
        agreement1.addToRoles(agreementRole1);
        // when
        agreement1.addToRoles(agreementRole1);
        
        assertThat(agreement1.getRoles(), Matchers.contains(agreementRole1));
        assertThat(agreementRole1.getAgreement(), is(agreement1));
    }
    
    @Ignore
    @Test
    public void addAtParentWhenNull() {
        agreement1.addToRoles(null);
        
        assertThat(agreement1.getRoles().isEmpty(), is(true));
    }
    
    @Ignore
    @Test
    public void addAtChild() {
        agreementRole1.modifyAgreement(agreement1);
        
        assertThat(agreement1.getRoles(), Matchers.contains(agreementRole1));
        assertThat(agreementRole1.getAgreement(), is(agreement1));
    }

    @Ignore
    @Test
    public void addAtChildWhenAlreadyParent() {
        // given
        agreementRole1.modifyAgreement(agreement1);
        // when
        agreementRole1.modifyAgreement(agreement1);
        
        assertThat(agreement1.getRoles(), Matchers.contains(agreementRole1));
        assertThat(agreementRole1.getAgreement(), is(agreement1));
    }


    @Ignore
    @Test
    public void addAtChildWhenNull() {
        agreementRole1.modifyAgreement(null);
        
        assertThat(agreementRole1.getAgreement(), is(nullValue()));
    }
    
    @Ignore
    @Test
    public void removeAtParent() {
        // given
        agreement1.addToRoles(agreementRole1);
        
        // when
        agreement1.removeFromRoles(agreementRole1);
        
        assertThat(agreement1.getRoles().isEmpty(), is(true));
        assertThat(agreementRole1.getAgreement(), is(nullValue()));
    }

    @Ignore
    @Test
    public void removeAtParentWhenNull() {
        // given
        agreement1.addToRoles(agreementRole1);
        
        // when
        agreement1.removeFromRoles(null);
        
        assertThat(agreement1.getRoles(), Matchers.contains(agreementRole1));
        assertThat(agreementRole1.getAgreement(), is(agreement1));
    }
    
    @Ignore
    @Test
    public void removeAtParentWhenNotAssociated() {
        // given
        agreement1.addToRoles(agreementRole1);
        
        // when
        agreement1.removeFromRoles(agreementRole2);
        
        assertThat(agreement1.getRoles(), Matchers.contains(agreementRole1));
        assertThat(agreementRole1.getAgreement(), is(agreement1));
    }
    
    @Ignore
    @Test
    public void removeAtChild() {
        // given
        agreement1.addToRoles(agreementRole1);
        
        // when
        agreementRole1.clearAgreement();
        
        assertThat(agreement1.getRoles().isEmpty(), is(true));
        assertThat(agreementRole1.getAgreement(), is(nullValue()));
    }

    @Ignore
    @Test
    public void move() {
        // given
        agreement1.addToRoles(agreementRole1);
        agreement2.addToRoles(agreementRole2);
        
        // when
        agreementRole1.modifyAgreement(agreement2);
        
        assertThat(agreement1.getRoles().isEmpty(), is(true));
        assertThat(agreement2.getRoles(), Matchers.contains(agreementRole1, agreementRole2));
        assertThat(agreementRole1.getAgreement(), is(agreement2));
        assertThat(agreementRole2.getAgreement(), is(agreement2));
    }
    
    @Ignore
    @Test
    public void moveWhenAlreadyAssociated() {
        // given
        agreement1.addToRoles(agreementRole1);
        agreement2.addToRoles(agreementRole2);
        
        // when
        agreementRole1.modifyAgreement(agreement1);
        
        assertThat(agreement1.getRoles(), Matchers.contains(agreementRole1));
        assertThat(agreement2.getRoles(), Matchers.contains(agreementRole2));
        assertThat(agreementRole1.getAgreement(), is(agreement1));
        assertThat(agreementRole2.getAgreement(), is(agreement2));
    }

}
