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

import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectItemTerm;
import org.estatio.module.capex.dom.project.ProjectItemTermRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProjectItemTermRepository_IntegTest extends CapexModuleIntegTestAbstract {

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
        final LocalDate itemStartDate = new LocalDate(2018, 1, 1);
        final LocalDate itemEndDate = new LocalDate(2018, 3, 31);
        Project projectForKal = Project_enum.KalProject1.findUsing(serviceRegistry);
        projectForKal.addItem(Charge_enum.NlIncomingCharge1.findUsing(serviceRegistry), "Some item", new BigDecimal("150000.00"),  itemStartDate, itemEndDate,null, null);
        final ProjectItem firstItem = projectForKal.getItems().first();
        assertThat(firstItem.getProjectItemTerms()).isEmpty();

        // when
        final LocalDate startDate = new LocalDate(2018, 1, 1);
        final LocalDate endDate = new LocalDate(2018, 3, 31);
        final BigDecimal amount = new BigDecimal("10000.00");
        firstItem.newProjectItemTerm(amount, startDate, endDate);

        // then
        assertThat(projectTermRepository.listAll()).hasSize(1);
        assertThat(firstItem.getProjectItemTerms()).hasSize(1);
        ProjectItemTerm projectItemTerm = firstItem.getProjectItemTerms().get(0);
        assertThat(projectItemTerm.getProjectItem()).isEqualTo(firstItem);
        assertThat(projectItemTerm.getBudgetedAmount()).isEqualTo(amount);
        assertThat(projectItemTerm.getStartDate()).isEqualTo(startDate);
        assertThat(projectItemTerm.getEndDate()).isEqualTo(endDate);

        // and when
        final BigDecimal otherAmount = new BigDecimal("12345.00");
        firstItem.newProjectItemTerm(otherAmount, startDate, endDate);

        // then still
        assertThat(firstItem.getProjectItemTerms()).hasSize(1);
        assertThat(firstItem.getProjectItemTerms().get(0).getBudgetedAmount()).isEqualTo(amount);

        // and when
        final BigDecimal amount2 = new BigDecimal("20000.00");
        final LocalDate startDate2 = new LocalDate(2018, 4, 1);
        final LocalDate endDate2 = new LocalDate(2018, 6, 30);
        firstItem.newProjectItemTerm(amount2, startDate2, endDate2);

        // then sorted by date asc
        assertThat(firstItem.getProjectItemTerms()).hasSize(2);
        assertThat(firstItem.getProjectItemTerms().get(0).getProjectItem()).isEqualTo(firstItem);
        assertThat(firstItem.getProjectItemTerms().get(0).getStartDate()).isEqualTo(startDate);
        assertThat(firstItem.getProjectItemTerms().get(0).getEndDate()).isEqualTo(endDate);
        assertThat(firstItem.getProjectItemTerms().get(0).getBudgetedAmount()).isEqualTo(amount);
        assertThat(firstItem.getProjectItemTerms().get(1).getProjectItem()).isEqualTo(firstItem);
        assertThat(firstItem.getProjectItemTerms().get(1).getStartDate()).isEqualTo(startDate2);
        assertThat(firstItem.getProjectItemTerms().get(1).getEndDate()).isEqualTo(endDate2);
        assertThat(firstItem.getProjectItemTerms().get(1).getBudgetedAmount()).isEqualTo(amount2);

    }

    @Inject
    ProjectItemTermRepository projectTermRepository;

}