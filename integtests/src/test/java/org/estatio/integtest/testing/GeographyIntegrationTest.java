package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.junit.Test;

public class GeographyIntegrationTest extends AbstractIntegrationTest {



    @Test
    public void countryIsNL() throws Exception {
        Assert.assertEquals("NLD", countries.findByReference("NLD").getReference());
    }

    @Test
    public void stateCanBeFound() throws Exception {
        assertThat(states.findByCountry(countries.findByReference("NLD")).size(), is(1));
    }
}
