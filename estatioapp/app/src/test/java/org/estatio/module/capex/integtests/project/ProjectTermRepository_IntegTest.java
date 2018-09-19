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
package org.estatio.module.capex.integtests.project;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.dom.project.ProjectTerm;
import org.estatio.module.capex.dom.project.ProjectTermRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProjectTermRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Project_enum.KalProject1.builder());
            }
        });
    }

    @Test
    public void create_project_term_works() throws Exception {

        // given
        Project projectForKal = Project_enum.KalProject1.findUsing(serviceRegistry);
        assertThat(projectForKal.getProjectTerms()).isEmpty();

        // when
        final BigDecimal amount = new BigDecimal("10000.00");
        final LocalDate startDate = new LocalDate(2018, 1, 1);
        final LocalDate endDate = new LocalDate(2018, 3, 31);
        projectForKal.newProjectTerm(amount, startDate, endDate);

        // then
        assertThat(projectTermRepository.listAll()).hasSize(1);
        assertThat(projectForKal.getProjectTerms()).hasSize(1);
        ProjectTerm budget = projectForKal.getProjectTerms().get(0);
        assertThat(budget.getProject()).isEqualTo(projectForKal);
        assertThat(budget.getBudgetedAmount()).isEqualTo(amount);
        assertThat(budget.getStartDate()).isEqualTo(startDate);
        assertThat(budget.getEndDate()).isEqualTo(endDate);

        // and when
        final BigDecimal otherAmount = new BigDecimal("12345.00");
        projectForKal.newProjectTerm(otherAmount, startDate, endDate);

        // then still
        assertThat(projectForKal.getProjectTerms()).hasSize(1);
        assertThat(projectForKal.getProjectTerms().get(0).getBudgetedAmount()).isEqualTo(amount);

        // and when
        final BigDecimal amount2 = new BigDecimal("20000.00");
        final LocalDate startDate2 = new LocalDate(2018, 4, 1);
        final LocalDate endDate2 = new LocalDate(2018, 6, 30);
        projectForKal.newProjectTerm(amount2, startDate2, endDate2);

        // then sorted by date desc
        assertThat(projectForKal.getProjectTerms()).hasSize(2);
        assertThat(projectForKal.getProjectTerms().get(0).getStartDate()).isEqualTo(startDate2);
        assertThat(projectForKal.getProjectTerms().get(0).getEndDate()).isEqualTo(endDate2);
        assertThat(projectForKal.getProjectTerms().get(0).getBudgetedAmount()).isEqualTo(amount2);
        assertThat(projectForKal.getProjectTerms().get(1).getStartDate()).isEqualTo(startDate);

    }

    @Inject
    ProjectTermRepository projectTermRepository;

}