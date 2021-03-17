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
package org.estatio.module.capex.fixtures.project.builders;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectItemRepository;
import org.estatio.module.capex.dom.project.ProjectItemTerm;
import org.estatio.module.capex.dom.project.ProjectItemTermRepository;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"projectReference", "chargeReference", "startDate"}, callSuper = false)
@ToString(of={"projectReference", "chargeReference", "startDate"})
@Accessors(chain = true)
public final class ProjectItemTermBuilder extends BuilderScriptAbstract<ProjectItemTerm, ProjectItemTermBuilder> {

    @Getter @Setter
    private String projectReference;
    @Getter @Setter
    private String chargeReference;
    @Getter @Setter
    private BigDecimal budgetedAmount;
    @Getter @Setter
    private LocalDate startDate;
    @Getter @Setter
    private LocalDate endDate;

    @Getter
    private ProjectItemTerm object;

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("projectReference", ec, ProjectItem.class);
        checkParam("chargeReference", ec, ProjectItem.class);
        checkParam("budgetedAmount", ec, BigDecimal.class);
        checkParam("startDate", ec, LocalDate.class);
        checkParam("endDate", ec, LocalDate.class);

        Project project = projectRepository.findByReference(projectReference);
        Charge charge = chargeRepository.findByReference(chargeReference);
        ProjectItem projectItem = projectItemRepository.findByProjectAndCharge(project, charge);

        object = projectItemTermRepository.findOrCreate(projectItem, budgetedAmount, startDate, endDate);
    }


    @Inject
    protected ProjectItemTermRepository projectItemTermRepository;

    @Inject
    protected ProjectRepository projectRepository;

    @Inject
    protected ProjectItemRepository projectItemRepository;

    @Inject
    protected ChargeRepository chargeRepository;

}
