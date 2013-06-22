package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;

public class AgreementsTest_findByReference {

    private QueryDefault<?> queryDefault;

    private Agreements agreements;

    @Before
    public void setup() {
        agreements = new Agreements() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                queryDefault = (QueryDefault<?>) query;
                return null;
            }
        };
    }

    
    @Test
    public void happyCase() {

        agreements.findByReference("*some?Reference*");
        
        // then
        assertThat(queryDefault.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
        assertThat(queryDefault.getArgumentsByParameterName().get("r"), is((Object)".*some.Reference.*"));
        assertThat(queryDefault.getArgumentsByParameterName().size(), is(1));
    }

}
