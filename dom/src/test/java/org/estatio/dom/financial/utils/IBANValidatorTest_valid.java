package org.estatio.dom.financial.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class IBANValidatorTest_valid {

    private IBANValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new IBANValidator();
    }

    @Test
    public void happyCase() {
        assertThat(validator.valid("NL26INGB0680433600"), is(true));
        assertThat(validator.valid("NL07INGB0697694704"), is(true));
        assertThat(validator.valid("IT69N0347501601000051986922"), is(true));
        assertThat(validator.valid("IT93Q0347501601000051768165"), is(true));
    }
    
    @Test
    public void sadCase() {
        assertThat(validator.valid("NLXXINGB0680433600"), is(false));
    }
}
