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
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;

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
            final Country countryIfAny) {

        final ApplicationTenancy applicationTenancy =  estatioApplicationTenancyRepository.findOrCreateTenancyFor(countryIfAny);
        return newProgram(reference, name, programGoal, applicationTenancy);

    }

    @Programmatic
    public Program newProgram(
            final String reference,
            final String name,
            final String programGoal,
            final ApplicationTenancy applicationTenancy) {
        // Create project instance
        Program program = repositoryService.instantiate(Program.class);

        // Set values
        program.setReference(reference);
        program.setName(name);
        program.setProgramGoal(programGoal);
        program.setApplicationTenancyPath(applicationTenancy.getPath());

        // Persist it
        repositoryService.persist(program);

        // Return it
        return program;
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
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    RepositoryService repositoryService;
}
