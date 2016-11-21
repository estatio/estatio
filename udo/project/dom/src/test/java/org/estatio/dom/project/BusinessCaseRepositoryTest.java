package org.estatio.dom.project;

import java.util.List;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class BusinessCaseRepositoryTest {
	
    FinderInteraction finderInteraction;

    BusinessCaseRepository businessCaseRepository;

    @Before
    public void setup() {
    	businessCaseRepository = new BusinessCaseRepository() {

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
    
    public static class BusinessCaseHistory extends BusinessCaseRepositoryTest {
    	
        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
        
        @Mock
        private Project project;

        @Test
        public void happyCase() {

        	businessCaseRepository.businessCaseHistory(project);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BusinessCase.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByProject");
            assertThat(finderInteraction.getArgumentsByParameterName().get("project")).isEqualTo((Object) project);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }
    
    public static class ActiveBusinessCaseOnProject extends BusinessCaseRepositoryTest {
    	
        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
        
        @Mock
        private Project project;

        @Test
        public void happyCase() {

        	businessCaseRepository.findActiveBusinessCaseOnProject(project);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(BusinessCase.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findActiveBusinessCaseOnProject");
            assertThat(finderInteraction.getArgumentsByParameterName().get("project")).isEqualTo((Object) project);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }
    
}
