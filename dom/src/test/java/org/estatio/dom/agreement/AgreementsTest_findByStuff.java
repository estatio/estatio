package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementsTest_findByStuff {

    private QueryDefault<?> queryDefault;

    private Agreements agreements;

    private AgreementType agreementType;
    private AgreementRoleType agreementRoleType;
    private Party party;

    @Before
    public void setup() {
        
        agreementType = new AgreementType();
        agreementRoleType = new AgreementRoleType();
        party = new PartyForTesting();
        
        agreements = new Agreements() {

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                queryDefault = (QueryDefault<?>) query;
                return null;
            }
        };
    }

    
    @Test
    public void happyCase() {

        agreements.findByAgreementTypeAndRoleTypeAndParty(agreementType, agreementRoleType, party);
        
        // then
        assertThat(queryDefault.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
        assertThat(queryDefault.getArgumentsByParameterName().get("agreementType"), is((Object)agreementType));
        assertThat(queryDefault.getArgumentsByParameterName().get("roleType"), is((Object)agreementRoleType));
        assertThat(queryDefault.getArgumentsByParameterName().get("party"), is((Object)party));
        assertThat(queryDefault.getArgumentsByParameterName().size(), is(3));
    }

}
