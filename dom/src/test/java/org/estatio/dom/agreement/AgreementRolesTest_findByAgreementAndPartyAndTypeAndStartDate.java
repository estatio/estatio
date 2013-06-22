package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementRolesTest_findByAgreementAndPartyAndTypeAndStartDate {

    private Agreement agreement;
    private Party party;
    private AgreementRoleType type;
    private LocalDate startDate;

    private QueryDefault<?> queryDefault;

    private AgreementRoles agreementRoles;

    @Before
    public void setup() {
        agreement = new AgreementForTesting();
        party = new PartyForTesting();
        type = new AgreementRoleType();
        startDate = new LocalDate(2013,4,1);
        
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


        agreementRoles.findByAgreementAndPartyAndTypeAndStartDate(agreement, party, type, startDate);
        
        // then
        assertThat(queryDefault.getResultType(), IsisMatchers.classEqualTo(AgreementRole.class));
        assertThat(queryDefault.getArgumentsByParameterName().get("agreement"), is((Object)agreement));
        assertThat(queryDefault.getArgumentsByParameterName().get("party"), is((Object)party));
        assertThat(queryDefault.getArgumentsByParameterName().get("type"), is((Object)type));
        assertThat(queryDefault.getArgumentsByParameterName().get("startDate"), is((Object)startDate));
        assertThat(queryDefault.getArgumentsByParameterName().size(), is(4));
    }

}
