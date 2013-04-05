package org.estatio.dom.utils;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IBANValidatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        IBANValidator validator = new IBANValidator();
        Assert.assertThat(validator.valid("NL26INGB0680433600"), Is.is(true));
        Assert.assertThat(validator.valid("NLXXINGB0680433600"), Is.is(false));
        Assert.assertThat(validator.valid("NL07INGB0697694704"), Is.is(true));
        Assert.assertThat(validator.valid("IT69N0347501601000051986922"), Is.is(true));
        Assert.assertThat(validator.valid("IT93Q0347501601000051768165"), Is.is(true));
    }
}
