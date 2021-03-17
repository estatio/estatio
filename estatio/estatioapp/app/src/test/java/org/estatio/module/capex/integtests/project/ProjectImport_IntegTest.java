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

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.app.ProjectMenu;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectItemTerm;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.project.ProjectItemTermRepository;
import org.estatio.module.capex.imports.ProjectImportManager;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.EstatioChargeModule;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProjectImport_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Property_enum.OxfGb.builder());
            }
        });
    }

    @Test
    public void project_import_works() throws Exception {

        // given
        Charge works = chargeRepository.findOrCreate("/GBR", "WORKS", "WORKS", "some works", Applicability.INCOMING);
        Charge legal = chargeRepository.findOrCreate("/GBR", "LEGAL / BAILIFF FEES", "legal stuff", "other stuff", Applicability.INCOMING);
        Charge other = chargeRepository.findOrCreate("/GBR", "OTHER", "OTHER", "other stuff", Applicability.INCOMING);

        Property oxf = Property_enum.OxfGb.findUsing(serviceRegistry);

        final URL url = Resources.getResource(ProjectImport_IntegTest.class, "Projects OXF.xlsx");
        byte[] bytes;
        try {
            bytes = Resources.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Blob spreadSheet = new Blob("Projects OXF.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        assertThat(projectRepository.listAll()).isEmpty();

        // when
        ProjectImportManager manager = wrap(projectMenu).importProjects(oxf.getCountry(), null);
        wrap(manager).upload(spreadSheet);

        // then
        assertThat(projectRepository.listAll()).hasSize(3);

        Project parent = projectRepository.findByReference("GBPP");
        assertThat(parent.getName()).isEqualTo("Parent Project");
        assertThat(parent.getAtPath()).isEqualTo("/GBR");
        assertThat(parent.getItems()).isEmpty();
        assertThat(parent.getStartDate()).isNull();
        assertThat(parent.getEndDate()).isNull();
        assertThat(parent.getParent()).isNull();

        Project project1 = projectRepository.findByReference("GB01");
        assertThat(project1.getName()).isEqualTo("Project 1");
        assertThat(project1.getAtPath()).isEqualTo("/GBR");
        assertThat(project1.getItems()).hasSize(2);
        assertThat(project1.getStartDate()).isEqualTo(new LocalDate(2018,1,1));
        assertThat(project1.getEndDate()).isNull();
        assertThat(project1.getParent()).isEqualTo(parent);

        ProjectItem project1item1 = project1.getItems().first();
        assertThat(project1item1.getCharge()).isEqualTo(other);
        assertThat(project1item1.getDescription()).isEqualTo("other stuff");
        assertThat(project1item1.getBudgetedAmount()).isEqualTo(new BigDecimal("100000.5"));
        assertThat(project1item1.getStartDate()).isEqualTo(new LocalDate(2018,1,1));
        assertThat(project1item1.getEndDate()).isNull();
        assertThat(project1item1.getProperty()).isEqualTo(oxf);
        assertThat(project1item1.getTax()).isNull();

        ProjectItem project1item2 = project1.getItems().last();
        assertThat(project1item2.getCharge()).isEqualTo(works);
        assertThat(project1item2.getDescription()).isEqualTo("some works");
        assertThat(project1item2.getBudgetedAmount()).isEqualTo(new BigDecimal("20000.0"));
        assertThat(project1item2.getStartDate()).isEqualTo(new LocalDate(2018,1,1));
        assertThat(project1item2.getEndDate()).isEqualTo(new LocalDate(2018,12,31));
        assertThat(project1item2.getProperty()).isEqualTo(oxf);
        assertThat(project1item2.getTax()).isNull();

        Project project2 = projectRepository.findByReference("GB02");
        assertThat(project2.getName()).isEqualTo("Project 2");
        assertThat(project2.getAtPath()).isEqualTo("/GBR");
        assertThat(project2.getItems()).hasSize(1);
        assertThat(project2.getStartDate()).isNull();
        assertThat(project2.getEndDate()).isNull();
        assertThat(project2.getParent()).isNull();

        ProjectItem project2item1 = project2.getItems().first();
        assertThat(project2item1.getCharge()).isEqualTo(legal);
        assertThat(project2item1.getDescription()).isEqualTo("legal stuff");
        assertThat(project2item1.getBudgetedAmount()).isEqualTo(new BigDecimal("50123.12"));
        assertThat(project2item1.getStartDate()).isNull();
        assertThat(project2item1.getEndDate()).isNull();
        assertThat(project2item1.getProperty()).isEqualTo(oxf);
        assertThat(project2item1.getTax()).isNull();

        assertThat(projectItemTermRepository.listAll()).hasSize(4);
        assertThat(project1item1.getProjectItemTerms()).hasSize(2);
        final ProjectItemTerm projectItemTerm = project1item1.getProjectItemTerms().get(0);
        assertThat(projectItemTerm.getProjectItem()).isEqualTo(project1item1);
        assertThat(projectItemTerm.getBudgetedAmount()).isEqualTo(new BigDecimal("40000.0"));
        assertThat(projectItemTerm.getStartDate()).isEqualTo(new LocalDate(2018,1,1));
        assertThat(projectItemTerm.getEndDate()).isEqualTo(new LocalDate(2018,3,31));

    }

    @After
    public void cleanRefData(){
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                projectItemTermRepository.listAll().forEach(t->repositoryService.remove(t));
                projectRepository.listAll().forEach(p->p.delete());
                transactionService.flushTransaction();
                executionContext.executeChild(this, new EstatioChargeModule().getRefDataTeardown());
            }
        });
    }

    @Inject ProjectMenu projectMenu;

    @Inject ProjectRepository projectRepository;

    @Inject ProjectItemTermRepository projectItemTermRepository;

    @Inject RepositoryService repositoryService;

    @Inject ChargeRepository chargeRepository;

}