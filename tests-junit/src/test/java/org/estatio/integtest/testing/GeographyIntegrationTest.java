package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.States;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

public class GeographyIntegrationTest {

    @Rule
    public IntegrationSystemForTestRule webServerRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return webServerRule.getIsisSystemForTest();
    }

    @Test
    public void countryIsNL() throws Exception {
        Countries c = getIsft().getService(Countries.class);
        Assert.assertEquals("NLD", c.findByReference("NLD").getReference());
    }

    @Test
    public void stateCanBeFound() throws Exception {
        States states = getIsft().getService(States.class);
        Countries countries = getIsft().getService(Countries.class);
        assertThat(states.findByCountry(countries.findByReference("NLD")).size(), is(1));
    }
}
