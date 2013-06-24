package org.estatio.dom.geography;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

public class StatesTest_finders {

    private FinderInteraction finderInteraction;

    private States states;

    private Country country;

    @Before
    public void setup() {
        
        country = new Country();
        
        states = new States() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<State> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }
            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    @Test
    public void findStateByReference() {

        states.findStateByReference("*REF?1*");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(State.class));
        assertThat(finderInteraction.getQueryName(), is("findByReference"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)".*REF.1.*"));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }

    @Test
    public void findStatesByCountry() {
        
        states.findStatesByCountry(country);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(State.class));
        assertThat(finderInteraction.getQueryName(), is("findByCountry"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("country"), is((Object)country));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }
    
    @Test
    public void allStates() {
        
        states.allStates();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
