package org.estatio.dom.numerator;

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

public class NumeratorsTest_finders {

    private FinderInteraction finderInteraction;

    private Numerators numerators;

    @Before
    public void setup() {

        numerators = new Numerators() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<Numerator> allInstances() {
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
    public void findNumeratorByType() {

        numerators.findNumeratorByType(NumeratorType.COLLECTION_NUMBER);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Numerator.class));
        assertThat(finderInteraction.getQueryName(), is("findByType"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("type"), is((Object)NumeratorType.COLLECTION_NUMBER));

        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }
    

    @Test
    public void allNumerators() {
        
        numerators.allNumerators();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
