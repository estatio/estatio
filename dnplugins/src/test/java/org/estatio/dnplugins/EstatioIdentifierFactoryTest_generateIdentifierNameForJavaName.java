package org.estatio.dnplugins;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.datanucleus.store.rdbms.identifier.IdentifierCase;
import org.estatio.dnplugins.EstatioIdentifierFactory;
import org.junit.Test;

public class EstatioIdentifierFactoryTest_generateIdentifierNameForJavaName {


    @Test
    public void whenContainsPeriod() {
        final String actual = EstatioIdentifierFactory.generateIdentifierNameForJavaName("nextTerm.LEASETERM_ID", IdentifierCase.UPPER_CASE, "_");
        //previously was: 
        // assertThat(actual, is("NEXTTERM_LEASETERM_ID"));
        assertThat(actual, is("NEXTTERM_ID"));
    }

    @Test
    public void whenDoesNotContainPeriod() {
        final String actual = EstatioIdentifierFactory.generateIdentifierNameForJavaName("LEASETERM_ID", IdentifierCase.UPPER_CASE, "_");
        assertThat(actual, is("LEASETERM_ID"));
    }

}
