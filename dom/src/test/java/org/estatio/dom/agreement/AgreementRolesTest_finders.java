package org.estatio.dom.agreement;

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
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementRolesTest_finders {

    private FinderInteraction finderInteraction;

    private Party party;

    private Agreement agreement;
    private AgreementRoleType type;
    private LocalDate date;

    private AgreementRoles agreementRoles;

    @Before
    public void setup() {

        party = new PartyForTesting();

        agreement = new AgreementForTesting();
        type = new AgreementRoleType();
        date = new LocalDate(2013,4,1);
        
        agreementRoles = new AgreementRoles() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<AgreementRole> allInstances() {
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
    public void findByAgreementAndTypeAndContainsDate() {


        agreementRoles.findByAgreementAndTypeAndContainsDate(agreement, type, date);
        
        // then
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(AgreementRole.class));
        assertThat(finderInteraction.getQueryName(), is("findByAgreementAndTypeAndContainsDate"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("agreement"), is((Object)agreement));
        assertThat(finderInteraction.getArgumentsByParameterName().get("type"), is((Object)type));
        assertThat(finderInteraction.getArgumentsByParameterName().get("date"), is((Object)date));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
    }

    @Test
    public void findByAgreementAndPartyAndTypeAndStartDate() {

        agreementRoles.findByAgreementAndPartyAndTypeAndStartDate(agreement, party, type, date);
        
        // then
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(AgreementRole.class));
        assertThat(finderInteraction.getQueryName(), is("findByAgreementAndPartyAndTypeAndStartDate"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("agreement"), is((Object)agreement));
        assertThat(finderInteraction.getArgumentsByParameterName().get("party"), is((Object)party));
        assertThat(finderInteraction.getArgumentsByParameterName().get("type"), is((Object)type));
        assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object)date));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(4));
    }

}
