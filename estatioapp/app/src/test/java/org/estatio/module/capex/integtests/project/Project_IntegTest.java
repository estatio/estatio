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

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class Project_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Project_enum.KalProject1.builder());
                executionContext.executeChild(this, Project_enum.KalProject2.builder());
            }
        });
    }

    @Test
    public void create_parent_project_works() throws Exception {

        // given
        Project child = Project_enum.KalProject1.findUsing(serviceRegistry);

        // when
        final String reference = "KAL-PARENT";
        final String name = "Kal Parent Project";
        Project parent = wrap(child).createParentProject(reference, name, null, null);

        // then
        assertThat(child.getParent()).isEqualTo(parent);
        assertThat(parent.getChildren()).contains(child);
        assertThat(parent.getReference()).isEqualTo(reference);
        assertThat(parent.getName()).isEqualTo(name);

    }

    @Test
    public void addChildProject_works() throws Exception {

        // given
        Project parent = Project_enum.KalProject1.findUsing(serviceRegistry);
        Project child = Project_enum.KalProject2.findUsing(serviceRegistry);

        // when
        wrap(parent).addChildProject(child);
        transactionService.nextTransaction();

        // then
        assertThat(child.getParent()).isEqualTo(parent);
        assertThat(parent.getChildren()).contains(child);

    }

    @Test
    public void parent_project_cannot_add_itself_as_a_child() throws Exception {

        // given
        Project parent = Project_enum.KalProject1.findUsing(serviceRegistry);

        // expect
        expectedExceptions.expect(InvalidException.class);
        expectedExceptions.expectMessage("Reason: A project cannot have itself as a child.");

        // when
        wrap(parent).addChildProject(parent);

    }

    @Test
    public void parent_project_cannot_add_a_child_that_already_has_a_parent() throws Exception {

        // given
        Project parent = Project_enum.KalProject1.findUsing(serviceRegistry);
        Project child = Project_enum.KalProject2.findUsing(serviceRegistry);
        wrap(child).createParentProject("Some ref", "some name", null, null);

        // expect
        expectedExceptions.expect(InvalidException.class);
        expectedExceptions.expectMessage("Reason: The child project is linked to a parent already.");

        // when
        wrap(parent).addChildProject(child);

    }



}