package org.estatio.dom.asset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

public class FixedAssetsTest_finders {

    private FinderInteraction finderInteraction;

    private FixedAssets fixedAssets;

    @Before
    public void setup() {
        fixedAssets = new FixedAssets() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
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
    public void findAssetsByReferenceOrName() {

        fixedAssets.findAssetsByReferenceOrName("some?search*Phrase");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(FixedAsset.class));
        assertThat(finderInteraction.getQueryName(), is("findAssetsByReferenceOrName"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("regex"), is((Object)"(?i)some.search.*Phrase"));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }
    
    @Test
    public void autoComplete() {
        
        fixedAssets.autoComplete("some?RegEx*Phrase");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(FixedAsset.class));
        assertThat(finderInteraction.getQueryName(), is("findAssetsByReferenceOrName"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("regex"), is((Object)"(?i).*some.RegEx.*Phrase.*"));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }

}
