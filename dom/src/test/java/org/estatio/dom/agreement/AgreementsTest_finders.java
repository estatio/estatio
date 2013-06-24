package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementsTest_finders {

    private FinderInteraction finderInteraction;

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
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<Agreement> allInstances() {
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
    public void findByAgreementTypeAndRoleTypeAndParty() {

        agreements.findByAgreementTypeAndRoleTypeAndParty(agreementType, agreementRoleType, party);
        
        // then
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
        assertThat(finderInteraction.getQueryName(), is("findByAgreementTypeAndRoleTypeAndParty"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("agreementType"), is((Object)agreementType));
        assertThat(finderInteraction.getArgumentsByParameterName().get("roleType"), is((Object)agreementRoleType));
        assertThat(finderInteraction.getArgumentsByParameterName().get("party"), is((Object)party));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
    }


    @Test
    public void findAgreementByReference() {

        agreements.findAgreementByReference("*some?Reference*");
        
        // then
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
        assertThat(finderInteraction.getQueryName(), is("findAgreementByReference"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("r"), is((Object)".*some.Reference.*"));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }

}
