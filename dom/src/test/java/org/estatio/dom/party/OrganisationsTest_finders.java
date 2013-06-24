package org.estatio.dom.party;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;

public class OrganisationsTest_finders {

    private FinderInteraction finderInteraction;

    private Organisations organisations;

    @Before
    public void setup() {

        organisations = new Organisations() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<Organisation> allInstances() {
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
    public void findOrganisation() {

        organisations.findOrganisation("*REF?1*");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Organisation.class));
        assertThat(finderInteraction.getQueryName(), is("findByReferenceOrName"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("searchArg"), is((Object)"(?i).*REF.1.*"));

        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }
    
    
    @Test
    public void allOrganisations() {
        
        organisations.allOrganisations();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
