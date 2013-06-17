package org.estatio.objectstore.jdo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EstatioIdentifierFactoryTest_stripOidSuffixIfPresent {


    @Test
    public void whenIs() {
        final String actual = EstatioIdentifierFactory.stripOidSuffixIfPresent("ABC_ID_OID");
        assertThat(actual, is("ABC_ID"));
    }


    @Test
    public void whenIsNot() {
        final String actual = EstatioIdentifierFactory.stripOidSuffixIfPresent("ABC_ID_FOO");
        assertThat(actual, is("ABC_ID_FOO"));
    }

}
