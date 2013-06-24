package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;

public class GeographyIntegrationTest extends AbstractEstatioIntegrationTest {


    @Test
    public void countryIsNL() throws Exception {
        assertThat(countries.findCountryByReference("NLD").getReference(), is("NLD"));
    }

    @Test
    public void stateCanBeFound() throws Exception {
        final Country country = countries.findCountryByReference("NLD");
        final List<State> statesInCountry = states.findStatesByCountry(country);
        assertThat(statesInCountry.size(), Matchers.greaterThanOrEqualTo(1));
        for (State state : statesInCountry) {
            assertThat(state.getCountry(), is(country));
        }
    }
}
