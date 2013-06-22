package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;

public class AgreementRoleTypesTest_findApplicableTo {

    private QueryDefault<?> queryDefault;

    private AgreementRoleTypes agreementRoleTypes;

    private AgreementType agreementType;

    @Before
    public void setup() {
        agreementType = new AgreementType();
        
        agreementRoleTypes = new AgreementRoleTypes() {

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                queryDefault = (QueryDefault<?>) query;
                return null;
            }
        };
    }

    
    @Test
    public void happyCase() {

        agreementRoleTypes.findApplicableTo(agreementType);

        // then
        assertThat(queryDefault.getResultType(), IsisMatchers.classEqualTo(AgreementRoleType.class));
        assertThat(queryDefault.getArgumentsByParameterName().get("appliesTo"), is((Object)agreementType));
        assertThat(queryDefault.getArgumentsByParameterName().size(), is(1));
    }

}
