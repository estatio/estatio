/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;

import org.isisaddons.module.fakedata.dom.FakeDataService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.capex.app.ProjectMenu;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectMenu_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject
    ProjectMenu projectMenu;

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    FakeDataService fakeDataService;

    @Inject
    SudoService sudoService;

    private ApplicationTenancy itaAppTenancy;

    @Before
    public void setupData() {
        itaAppTenancy = ApplicationTenancy_enum.It.findUsing(serviceRegistry);
        runFixtureScript(new FixtureScript() {
            @Override protected void execute(final ExecutionContext ec) {
                ec.executeChild(this, Person_enum.LoredanaPropertyInvoiceMgrIt);
            }
        });
    }

    @Test
    public void scenario_for_italy() {

        // given
        List<Numerator> numerators = numeratorRepository.allNumerators();
        assertThat(numerators).isEmpty();

        // when, then
        final List<Project> projectsBefore = projectMenu.allProjects();
        assertThat(projectsBefore).isEmpty();

        // when
        String projName = fakeDataService.strings().upper(20);
        final LocalDate startDate = fakeDataService.jodaLocalDates().any();
        final LocalDate endDate = startDate.plusDays(fakeDataService.jodaPeriods().yearsBetween(1, 5).getDays());

        final Project project = sudoService.sudo(Person_enum.LoredanaPropertyInvoiceMgrIt.getRef().toLowerCase(),
                () -> wrap(projectMenu).newProjectItaly(projName, startDate, endDate)
        );
        transactionService.nextTransaction();

        // then
        assertThat(project.getApplicationTenancy()).isEqualTo(itaAppTenancy);
        assertThat(project.getName()).isEqualTo(projName);
        assertThat(project.getStartDate()).isEqualTo(startDate);
        assertThat(project.getEndDate()).isEqualTo(endDate);
        assertThat(project.getReference()).isEqualTo("ITPR001");

        final List<Project> projectsAfter = projectMenu.allProjects();
        assertThat(projectsAfter).hasSize(1);
        assertThat(projectsAfter.get(0)).isSameAs(project);

        // and also
        final List<Numerator> numeratorsAfter = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter).hasSize(1);
        final Numerator numerator = numeratorsAfter.get(0);
        assertThat(numerator.getFormat()).isEqualTo("ITPR%03d");
        assertThat(numerator.getApplicationTenancyPath()).isEqualTo("/ITA");
        assertThat(numerator.getName()).isEqualTo("Project number");

        // when
        final String projName2 = fakeDataService.strings().upper(20);
        final Project project2 = sudoService.sudo(Person_enum.LoredanaPropertyInvoiceMgrIt.getRef().toLowerCase(),
                () -> wrap(projectMenu).newProjectItaly(projName2, null, null)
        );
        transactionService.nextTransaction();

        // then
        assertThat(project2.getApplicationTenancy()).isEqualTo(itaAppTenancy);
        assertThat(project2.getName()).isEqualTo(projName2);
        assertThat(project2.getStartDate()).isNull();
        assertThat(project2.getEndDate()).isNull();
        assertThat(project2.getReference()).isEqualTo("ITPR002");

        final List<Project> projectsAfter2 = projectMenu.allProjects();
        assertThat(projectsAfter2).hasSize(2);
        assertThat(projectsAfter2.get(1)).isSameAs(project2);

        // then
        final List<Numerator> numeratorsAfter2 = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter2).hasSize(1);

    }


}