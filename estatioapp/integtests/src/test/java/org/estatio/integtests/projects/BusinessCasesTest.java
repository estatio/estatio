/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.integtests.projects;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.project.BusinessCase;
import org.estatio.dom.project.BusinessCaseContributions;
import org.estatio.dom.project.Program;
import org.estatio.dom.project.Programs;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.Projects;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class BusinessCasesTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
				executionContext.executeChild(this, new ApplicationTenancyForGlobal());
                executionContext.executeChild(this, new PropertyForKalNl());
                
            }
        });
    }

    @Inject
	BusinessCaseContributions businesscases;
    
    @Inject
    Projects projects;
    
    @Inject
    Programs programs;
    
    @Inject
    Properties properties;

	@Inject
	ApplicationTenancies applicationTenancies;


    public static class newBusinessCase extends BusinessCasesTest {
    	
    	BusinessCase bc;
    	Program p1;
    	Project pr1;
    	
    	private static final String BUSINESSCASE_DESCRIPTION = "This is a description";
    	private static final LocalDate REVIEWDATE = LocalDate.now().plusDays(7);
    	private static final LocalDate NOW = LocalDate.now();
		private static final String AT_PATH_GLOBAL = "/";
    	
    	
    	@Before
    	public void setUp() throws Exception {
    		// given
    		p1 = programs.newProgram("TST", "TestProgram", "TestGoal", applicationTenancies.findTenancyByPath(AT_PATH_GLOBAL));
    		pr1 = projects.newProject("PR4", "Testproject", new LocalDate(2015, 1, 1), new LocalDate(2015, 12, 31), null, null, null, p1);
    		
    		// when
    		bc = businesscases.newBusinessCase(pr1, BUSINESSCASE_DESCRIPTION, REVIEWDATE);
    	}

    	@Test
        public void valuesSet() throws Exception {
    		//then 
    		assertThat(businesscases.businessCase(pr1), is(bc));
    		assertNull(bc.getNext());
    		assertNull(bc.getPrevious());
    		assertThat(bc.getDate(), is(NOW));
    		assertThat(bc.getBusinessCaseVersion(), is(1));
    		assertThat(bc.getNextReviewDate(), is(REVIEWDATE));
    		assertNull(bc.getLastUpdated());
    		assertThat(bc.getDescription(), is(BUSINESSCASE_DESCRIPTION));
    		assertThat(bc.getProject(), is(pr1));
    		assertThat(bc.hideUpdateBusinessCase(BUSINESSCASE_DESCRIPTION, REVIEWDATE), is(false));
    		assertNull(bc.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION, REVIEWDATE));
    		assertThat(businesscases.hideNewBusinessCase(pr1, BUSINESSCASE_DESCRIPTION, REVIEWDATE), is(true));
    	}

    }
    
    public static class newBusinessCaseValidation extends BusinessCasesTest {
    	
    	BusinessCase bc;
    	Program p1;
    	Project pr1;
    	
    	private static final String BUSINESSCASE_DESCRIPTION = "This is a description";
    	private static final LocalDate WRONG_REVIEWDATE = LocalDate.now().minusDays(7);
		private static final String AT_PATH_GLOBAL = "/";
    	
    	@Before
    	public void setUp() throws Exception {
    		// given
    		p1 = programs.newProgram("TST", "TestProgram", "TestGoal", applicationTenancies.findTenancyByPath(AT_PATH_GLOBAL));
    		pr1 = projects.newProject("PR4", "Testproject", new LocalDate(2015,1,1), new LocalDate(2015,12,31), null, null, null, p1);
    		
    		// when
    		bc = businesscases.newBusinessCase(pr1, BUSINESSCASE_DESCRIPTION, WRONG_REVIEWDATE);
    	}

    	@Test
        public void valuesSet() throws Exception {
    		//then 
    		assertThat(businesscases.businessCase(pr1), is(bc));
    		assertThat(bc.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION, WRONG_REVIEWDATE), is("A review date should not be in the past"));
    	}

    }
    
    public static class updateBusinessCase extends BusinessCasesTest {
    	
    	BusinessCase bc;
    	BusinessCase bc_upd;
    	Program p1;
    	Project pr1;
    	
    	private static final String BUSINESSCASE_DESCRIPTION = "This is a description";
    	private static final String BUSINESSCASE_DESCRIPTION_UPDATED = "This is an updated description";
    	private static final LocalDate REVIEWDATE = LocalDate.now().plusDays(7);
    	private static final LocalDate REVIEWDATE_UPDATED = LocalDate.now().plusDays(14);
    	private static final LocalDate NOW = LocalDate.now();
		private static final String AT_PATH_GLOBAL = "/";
    	
    	
    	@Before
    	public void setUp() throws Exception {
    		// given
    		p1 = programs.newProgram("TST", "TestProgram", "TestGoal", applicationTenancies.findTenancyByPath(AT_PATH_GLOBAL));
    		pr1 = projects.newProject("PR4", "Testproject", new LocalDate(2015,1,1), new LocalDate(2015,12,31), null, null, null, p1);
    		bc = businesscases.newBusinessCase(pr1, BUSINESSCASE_DESCRIPTION, REVIEWDATE);
    		
    		// when
    		bc_upd = bc.updateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, REVIEWDATE_UPDATED);
    	}

    	@Test
        public void valuesSet() throws Exception {
    		//then 
    		assertThat(businesscases.businessCase(pr1), is(bc_upd));
    		assertNull(bc_upd.getNext());
    		assertThat(bc_upd.getPrevious(), is(bc));
    		assertThat(bc.getNext(), is(bc_upd));
    		assertNull(bc.getPrevious());
    		assertThat(bc_upd.getDate(), is(NOW));
    		assertThat(bc.getDate(), is(NOW));
    		assertThat(bc_upd.getBusinessCaseVersion(), is(2));
    		assertThat(bc.getBusinessCaseVersion(), is(1));
    		assertThat(bc_upd.getNextReviewDate(), is(REVIEWDATE_UPDATED));
    		assertThat(bc.getNextReviewDate(), is(REVIEWDATE));
    		assertThat(bc_upd.getLastUpdated(), is(NOW));
    		assertNull(bc.getLastUpdated());
    		assertThat(bc_upd.getDescription(), is(BUSINESSCASE_DESCRIPTION_UPDATED));
    		assertThat(bc.getDescription(), is(BUSINESSCASE_DESCRIPTION));
    		assertThat(bc_upd.getProject(), is(pr1));
    		assertThat(bc.getProject(), is(pr1));
    		assertThat(bc_upd.hideUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, REVIEWDATE), is(false));
    		assertThat(bc.hideUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, REVIEWDATE), is(true));
    		assertNull(bc_upd.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, REVIEWDATE));
    		assertThat(bc.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, REVIEWDATE), is("This is no active version of the business case and cannot be updated"));
    		assertThat(businesscases.hideNewBusinessCase(pr1, BUSINESSCASE_DESCRIPTION, REVIEWDATE), is(true));
    	}

    }
    
    public static class updateBusinessCaseValidation extends BusinessCasesTest {
    	
    	BusinessCase bc;
    	BusinessCase bc_upd;
    	Program p1;
    	Project pr1;
    	
    	private static final String BUSINESSCASE_DESCRIPTION = "This is a description";
    	private static final String BUSINESSCASE_DESCRIPTION_UPDATED = "This is an updated description";
    	private static final LocalDate REVIEWDATE = LocalDate.now().plusDays(7);
    	private static final LocalDate WRONG_REVIEWDATE_UPDATED = LocalDate.now().minusDays(1);
		private static final String AT_PATH_GLOBAL = "/";
    	
    	
    	@Before
    	public void setUp() throws Exception {
    		// given
    		p1 = programs.newProgram("TST", "TestProgram", "TestGoal", applicationTenancies.findTenancyByPath(AT_PATH_GLOBAL));
    		pr1 = projects.newProject("PR4", "Testproject", new LocalDate(2015,1,1), new LocalDate(2015,12,31), null, null, null, p1);
    		bc = businesscases.newBusinessCase(pr1, BUSINESSCASE_DESCRIPTION, REVIEWDATE);
    		
    		// when
    		bc_upd = bc.updateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, WRONG_REVIEWDATE_UPDATED);
    	}

    	@Test
        public void valuesSet() throws Exception {
    		//then 

    		assertThat(bc_upd.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, WRONG_REVIEWDATE_UPDATED), is("A review date should not be in the past"));
    		assertThat(bc.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, REVIEWDATE), is("This is no active version of the business case and cannot be updated"));
    	}

    }

}