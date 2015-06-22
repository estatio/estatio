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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.DomainServiceLayout.MenuBar;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Dflt;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancies;
import org.estatio.dom.asset.Property;
import org.estatio.dom.utils.StringUtils;

@DomainService(repositoryFor = Program.class, nature=NatureOfService.VIEW)
@DomainServiceLayout(menuOrder="35", menuBar=MenuBar.PRIMARY, named="Projects")
public class Programs extends UdoDomainRepositoryAndFactory<Program> {

    public Programs() {
        super(Programs.class, Program.class);
    }

    @Action(semantics=SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Program newProgram(
            final @ParameterLayout(named="Reference") String reference,
            final @ParameterLayout(named="Name") String name,
            final @ParameterLayout(named="programGoal", multiLine = 5) String programGoal,
            final @ParameterLayout(named="Application Tenancy") ApplicationTenancy applicationTenancy) {
        // Create project instance
        Program program = getContainer().newTransientInstance(Program.class);
        // Set values
        program.setReference(reference);
        program.setName(name);
        program.setProgramGoal(programGoal);
        program.setApplicationTenancyPath(applicationTenancy.getPath());

        // Persist it
        persistIfNotAlready(program);
        // Return it
        return program;
    }

    public List<ApplicationTenancy> choices3NewProgram() {
        return estatioApplicationTenancies.globalOrCountryTenanciesForCurrentUser();
    }

    public ApplicationTenancy default3NewProgram() {
        return Dflt.of(choices3NewProgram());
    }

    @Action(semantics=SemanticsOf.SAFE)
    public List<Program> allPrograms() {
        return allInstances();
    }

    @Action(semantics=SemanticsOf.SAFE)
    public List<Program> findProgram(@ParameterLayout(named="Name or reference") final String searchStr) {
        return allMatches("matchByReferenceOrName", "matcher", StringUtils.wildcardToCaseInsensitiveRegex(searchStr));
    }

    @NotInServiceMenu
    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_ASSOCIATION)
    public List<Program> programs(final Property property) {
        return allMatches("findByProperty", "property", property);
    }

    @Inject
    EstatioApplicationTenancies estatioApplicationTenancies;

}
