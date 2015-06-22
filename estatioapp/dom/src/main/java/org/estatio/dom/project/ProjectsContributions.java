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
package org.estatio.dom.project;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.currency.Currency;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class ProjectsContributions {

    @Action(semantics=SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Project newProject(
            final @ParameterLayout(named="Reference") String reference,
            final @ParameterLayout(named="Name") String name,
            final @ParameterLayout(named="Start date") @Parameter(optionality=Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named="End date") @Parameter(optionality=Optionality.OPTIONAL) LocalDate endDate,
            final @ParameterLayout(named="Currency") @Parameter(optionality=Optionality.OPTIONAL) Currency currency,
            final @ParameterLayout(named="Estimated Cost") @Parameter(optionality=Optionality.OPTIONAL) BigDecimal estimatedCost,
            final @ParameterLayout(named="Project phase") @Parameter(optionality=Optionality.OPTIONAL) ProjectPhase projectPhase,
            final Program program) {
        return projects.newProject(reference, name, startDate, endDate, currency, estimatedCost, projectPhase, program);
    }

	@ActionLayout(contributed = Contributed.AS_ASSOCIATION)
	@CollectionLayout(render=RenderType.EAGERLY)
	@MemberOrder(name = "Projects", sequence = "1")
	@Action(semantics = SemanticsOf.SAFE)
	public List<Project> projects(final Program program) {
		return projects.findByProgram(program);
	}

	@Inject
	Projects projects;
	
	@Inject
	Program programs;

}