package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;

public class AgreementRoleTypesTest_findByTitle {

    private QueryDefault<?> queryDefault;

    private AgreementRoleTypes agreementRoleTypes;

    @Before
    public void setup() {
        agreementRoleTypes = new AgreementRoleTypes() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                queryDefault = (QueryDefault<?>) query;
                return null;
            }
        };
    }

    
    @Test
    public void happyCase() {


        agreementRoleTypes.findByTitle("someTitle");
        
        // then
        assertThat(queryDefault.getResultType(), IsisMatchers.classEqualTo(AgreementRoleType.class));
        assertThat(queryDefault.getArgumentsByParameterName().get("title"), is((Object)"someTitle"));
        assertThat(queryDefault.getArgumentsByParameterName().size(), is(1));
    }

}
