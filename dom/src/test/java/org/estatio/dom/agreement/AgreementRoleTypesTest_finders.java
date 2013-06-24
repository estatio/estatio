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

public class AgreementRoleTypesTest_finders {

    private FinderInteraction finderInteraction;

    private AgreementRoleTypes agreementRoleTypes;

    private AgreementType agreementType;

    @Before
    public void setup() {
        agreementType = new AgreementType();
        
        agreementRoleTypes = new AgreementRoleTypes() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
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
    public void findApplicableTo() {

        agreementRoleTypes.findApplicableTo(agreementType);

        // then
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(AgreementRoleType.class));
        assertThat(finderInteraction.getQueryName(), is("findByAgreementType"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("appliesTo"), is((Object)agreementType));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }

    @Test
    public void findByTitle() {

        agreementRoleTypes.findByTitle("someTitle");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(AgreementRoleType.class));
        assertThat(finderInteraction.getQueryName(), is("findByTitle"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("title"), is((Object)"someTitle"));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }

}
