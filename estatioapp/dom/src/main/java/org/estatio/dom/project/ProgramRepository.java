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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Dflt;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.utils.StringUtils;

@DomainService(repositoryFor = Program.class, nature = NatureOfService.DOMAIN)
public class ProgramRepository extends UdoDomainRepositoryAndFactory<Program> {

    public ProgramRepository() {
        super(ProgramRepository.class, Program.class);
    }

    @Programmatic
    public Program newProgram(
            final String reference,
            final String name,
            final String programGoal,
            final ApplicationTenancy applicationTenancy) {
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

    @Programmatic
    public List<ApplicationTenancy> choices3NewProgram() {
        return estatioApplicationTenancyRepository.globalOrCountryTenanciesForCurrentUser();
    }

    @Programmatic
    public ApplicationTenancy default3NewProgram() {
        return Dflt.of(choices3NewProgram());
    }

    @Programmatic
    public List<Program> allPrograms() {
        return allInstances();
    }

    @Programmatic
    public List<Program> findProgram(String searchStr) {
        return allMatches("matchByReferenceOrName", "matcher", StringUtils.wildcardToCaseInsensitiveRegex(searchStr));
    }

    @Inject
    EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

}
