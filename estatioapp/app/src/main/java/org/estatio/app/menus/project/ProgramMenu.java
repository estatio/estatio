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

package org.estatio.app.menus.project;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.Dflt;
import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.country.CountryServiceForCurrentUser;
import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.dom.project.Program;
import org.estatio.dom.project.ProgramRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.project.ProgramMenu"
)
@DomainServiceLayout(
        menuOrder = "35",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Projects"
)
public class ProgramMenu {


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Program newProgram(
            final String reference,
            final String name,
            @ParameterLayout(multiLine = 5)
            final String programGoal,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(describedAs = "Leave blank for a program spanning multiple countries")
            final Country countryIfAny) {
        return programRepository.newProgram(reference, name, programGoal, countryIfAny);
    }

    @Programmatic
    public List<Country> choices3NewProgram() {
        return countryService.countriesForCurrentUser();
    }

    @Programmatic
    public Country default3NewProgram() {
        return Dflt.of(choices3NewProgram());
    }



    @Action(semantics = SemanticsOf.SAFE)
    public List<Program> allPrograms() {
        return programRepository.allPrograms();
    }



    @Action(semantics = SemanticsOf.SAFE)
    public List<Program> findProgram(@ParameterLayout(named = "Name or reference") final String searchStr) {
        return programRepository.findProgram(searchStr);
    }


    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    CountryServiceForCurrentUser countryService;

    @Inject
    ProgramRepository programRepository;
}
