package org.estatio.dom.index;

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

public class IndexValuesTest_finders {

    private FinderInteraction finderInteraction;

    private IndexValues indexValues;

    private Index index;

    private LocalDate startDate;

    @Before
    public void setup() {
        
        index = new Index();
        startDate = new LocalDate(2013,4,1);
        
        indexValues = new IndexValues() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<IndexValue> allInstances() {
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
    public void findIndexValueByIndexAndStartDate() {

        indexValues.findIndexValueByIndexAndStartDate(index, startDate);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(IndexValue.class));
        assertThat(finderInteraction.getQueryName(), is("findByIndexAndStartDate"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("index"), is((Object)index));
        assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object)startDate));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
    }

    
    @Test
    public void allIndexValues() {
        
        indexValues.allIndexValues();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
