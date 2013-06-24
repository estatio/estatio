package org.estatio.dom.charge;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;

public class ChargesTest_finders {

    private QueryDefault<?> queryDefault;

    private Charges charges;

    @Before
    public void setup() {
        charges = new Charges() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                queryDefault = (QueryDefault<?>) query;
                return null;
            }
        };
    }

    
    @Test
    public void findByReference() {

        charges.findChargeByReference("*REF?1*");
        
        assertThat(queryDefault.getResultType(), IsisMatchers.classEqualTo(Charge.class));
        assertThat(queryDefault.getQueryName(), is("findByReference"));
        assertThat(queryDefault.getArgumentsByParameterName().get("reference"), is((Object)".*REF.1.*"));
        assertThat(queryDefault.getArgumentsByParameterName().size(), is(1));
    }

}
