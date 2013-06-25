package org.estatio.dom.charge;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.Property;

public class ChargeGroupsTest_finders {

    private FinderInteraction finderInteraction;

    private ChargeGroups chargeGroups;

    @Before
    public void setup() {
        chargeGroups = new ChargeGroups() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<ChargeGroup> allInstances() {
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
    public void findByReference() {

        chargeGroups.findChargeGroupByReference("*REF?1*");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(ChargeGroup.class));
        assertThat(finderInteraction.getQueryName(), is("findByReference"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)".*REF.1.*"));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }

    @Test
    public void allChargeGroups() {
        
        chargeGroups.allChargeGroups();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }

}
