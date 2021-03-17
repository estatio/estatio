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
package org.estatio.module.capex.fixtures.project.enums;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectItemRepository;
import org.estatio.module.capex.dom.project.ProjectItemTerm;
import org.estatio.module.capex.dom.project.ProjectItemTermRepository;
import org.estatio.module.capex.fixtures.project.builders.ProjectItemTermBuilder;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum ProjectItemTerm_enum implements PersonaWithBuilderScript<ProjectItemTerm, ProjectItemTermBuilder>, PersonaWithFinder<ProjectItemTerm> {

    RonProjectItem1Term1    (Project_enum.RonProjectIt, IncomingCharge_enum.ItConstruction, bd("45000.00"), ld(2018, 1, 1), ld(2018, 3, 31)),
    RonProjectItem1Term2    (Project_enum.RonProjectIt, IncomingCharge_enum.ItConstruction, bd("45000.00"), ld(2018, 4, 1), ld(2018, 6, 30)),
    RonProjectItem2Term1    (Project_enum.RonProjectIt, IncomingCharge_enum.ItAcquisition, bd("50000.00"), ld(2018, 1, 1), ld(2018, 3, 31)),
    RonProjectItem2Term2    (Project_enum.RonProjectIt, IncomingCharge_enum.ItAcquisition, bd("100000.00"), ld(2018, 4, 1), ld(2018, 6, 30)),
    RonProjectItem2Term3    (Project_enum.RonProjectIt, IncomingCharge_enum.ItAcquisition, bd("100000.00"), ld(2018, 7, 1), ld(2018, 9, 30)),
    RonProjectItem2Term4    (Project_enum.RonProjectIt, IncomingCharge_enum.ItAcquisition, bd("50000.00"), ld(2018, 10, 1), ld(2018, 12, 31)),
    ;

    private final Project_enum project_enum;
    private final IncomingCharge_enum incomingCharge_enum;
    private final BigDecimal budgetedAmount;
    private final LocalDate startDate;
    private final LocalDate endDate;


    @Override
    public ProjectItemTermBuilder builder() {
        return new ProjectItemTermBuilder().setProjectReference(project_enum.getRef())
                .setChargeReference(incomingCharge_enum.getReference())
                .setBudgetedAmount(budgetedAmount)
                .setStartDate(startDate)
                .setEndDate(endDate)
                ;
    }

    @Override
    public ProjectItemTerm findUsing(final ServiceRegistry2 serviceRegistry) {
        ProjectItemTermRepository projectItemTermRepository = serviceRegistry.lookupService(ProjectItemTermRepository.class);
        ProjectItemRepository projectItemRepository = serviceRegistry.lookupService(ProjectItemRepository.class);
        ProjectItem projectItem = projectItemRepository.findByProjectAndCharge(project_enum.findUsing(serviceRegistry), incomingCharge_enum.findUsing(serviceRegistry));
        return projectItemTermRepository.findUnique(projectItem, startDate);
    }

}
