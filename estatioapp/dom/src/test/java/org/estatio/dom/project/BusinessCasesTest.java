package org.estatio.dom.project;

import java.util.List;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BusinessCasesTest {
	
    FinderInteraction finderInteraction;

    BusinessCases businessCases;

    @Before
    public void setup() {
    	businessCases = new BusinessCases() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<BusinessCase> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
            
            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
        };

    }
    
    public static class BusinessCaseHistory extends BusinessCasesTest {
    	
        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
        
        @Mock
        private Project project;

        @Test
        public void happyCase() {

        	businessCases.businessCaseHistory(project);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BusinessCase.class));
            assertThat(finderInteraction.getQueryName(), is("findByProject"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("project"), is((Object) project));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }
    
    public static class ActiveBusinessCaseOnProject extends BusinessCasesTest {
    	
        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
        
        @Mock
        private Project project;

        @Test
        public void happyCase() {

        	businessCases.findActiveBusinessCaseOnProject(project);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BusinessCase.class));
            assertThat(finderInteraction.getQueryName(), is("findActiveBusinessCaseOnProject"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("project"), is((Object) project));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }
    
}
