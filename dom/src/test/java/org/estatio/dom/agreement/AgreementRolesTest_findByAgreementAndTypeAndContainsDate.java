package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;

public class AgreementRolesTest_findByAgreementAndTypeAndContainsDate {

    private Agreement agreement;
    private AgreementRoleType type;
    private LocalDate date;

    private QueryDefault<?> queryDefault;

    private AgreementRoles agreementRoles;

    @Before
    public void setup() {
        agreement = new AgreementForTesting();
        type = new AgreementRoleType();
        date = new LocalDate(2013,4,1);
        
        agreementRoles = new AgreementRoles() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                queryDefault = (QueryDefault<?>) query;
                return null;
            }
        };
    }

    
    @Test
    public void happyCase() {


        agreementRoles.findByAgreementAndTypeAndContainsDate(agreement, type, date);
        
        // then
        assertThat(queryDefault.getResultType(), IsisMatchers.classEqualTo(AgreementRole.class));
        assertThat(queryDefault.getArgumentsByParameterName().get("agreement"), is((Object)agreement));
        assertThat(queryDefault.getArgumentsByParameterName().get("type"), is((Object)type));
        assertThat(queryDefault.getArgumentsByParameterName().get("date"), is((Object)date));
        assertThat(queryDefault.getArgumentsByParameterName().size(), is(3));
    }

}
