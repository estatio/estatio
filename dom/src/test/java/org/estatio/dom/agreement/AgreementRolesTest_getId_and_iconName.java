package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AgreementRolesTest_getId_and_iconName {

    @Test
    public void getId() {
        assertThat(new AgreementRoles().getId(), is("agreementRoles"));
    }
    
    @Test
    public void iconName() {
        assertThat(new AgreementRoles().iconName(), is("AgreementRole"));
    }


}
