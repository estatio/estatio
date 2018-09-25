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
import java.util.Arrays;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;

import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.project.builders.ProjectBuilder;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;
import org.estatio.module.tax.fixtures.data.Tax_enum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.asset.fixtures.property.enums.Property_enum.OxfGb;
import static org.estatio.module.asset.fixtures.property.enums.Property_enum.RonIt;
import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.Gb;
import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.It;
import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.Nl;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Project_enum implements PersonaWithBuilderScript<Project, ProjectBuilder>, PersonaWithFinder<Project> {

    OxfProject  ("OXF-02", "New extension", ld(2016, 1, 1), ld(2019, 7, 1), Gb,
            new ItemSpec[]{
                new ItemSpec(IncomingCharge_enum.FrWorks, "works", bd("40000.00"), null, null, OxfGb, null)
            },
            new TermSpec[]{}
    ),
    GraProject  ("PR3", "Place commercial signs", ld(1999, 1, 1), ld(1999, 7, 1), Nl,
            new ItemSpec[]{},
            new TermSpec[]{}
    ),
    KalProject1 ("PR1", "Augment parkingplace", ld(1999, 1, 1), ld(1999, 7, 1), Nl,
            new ItemSpec[]{},
            new TermSpec[]{}
    ),
    KalProject2 ("PR2", "Broaden entrance", ld(1999, 4, 1), ld(1999, 5, 1), Nl,
            new ItemSpec[]{},
            new TermSpec[]{}
    ),
    RonProject  ("RON-01", "New extension", ld(2018, 1, 1), ld(2021, 7, 1), It,
            new ItemSpec[]{
                    new ItemSpec(IncomingCharge_enum.ItPurchase, "purchase (acquisto)", bd("90000.00"), null, null, RonIt, null),
                    new ItemSpec(IncomingCharge_enum.ItConstruction, "construction (costruzione)", bd("3000000.00"), null, null, RonIt, null),

            },
            new TermSpec[]{
                    new TermSpec(bd("50000.00"), new LocalDate(2018,1,1), new LocalDate(2018,3,31)),
                    new TermSpec(bd("100000.00"), new LocalDate(2018,4,1), new LocalDate(2018,6,30)),
                    new TermSpec(bd("200000.00"), new LocalDate(2018,7,1), new LocalDate(2018,9,30)),
                    new TermSpec(bd("500000.00"), new LocalDate(2018,10,1), new LocalDate(2018,12,31))
            }
    ),
    ;

    @AllArgsConstructor
    @Data
    public static class ItemSpec {
        private final IncomingCharge_enum charge_d;
        private final String description;
        private final BigDecimal budgetedAmount;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final Property_enum property_d;
        private final Tax_enum tax_d;
    }
    @AllArgsConstructor
    @Data
    public static class TermSpec {
        private final BigDecimal budgetedAmount;
        private final LocalDate startDate;
        private final LocalDate endDate;
    }
    private final String ref;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ApplicationTenancy_enum applicationTenancy;
    private final ItemSpec[] itemSpecs;
    private final TermSpec[] termSpecs;

    @Override
    public ProjectBuilder builder() {
        return new ProjectBuilder().setReference(ref)
                .setName(name)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setEstimatedCost(null)
                .setAtPath(applicationTenancy.getPath())
                .setParent(null)
                .setPrereq((f,ec) -> f.setItemSpecs(Arrays.stream(itemSpecs)
                        .map(x -> new ProjectBuilder.ItemSpec(
                                f.findUsing(x.charge_d),
                                x.description,
                                x.budgetedAmount,
                                x.startDate,
                                x.endDate,
                                f.objectFor(x.property_d, ec),
                                f.objectFor(x.tax_d, ec)))
                        .collect(Collectors.toList())))
                .setPrereq((f,ec) -> f.setTermSpecs(Arrays.stream(termSpecs)
                        .map(x -> new ProjectBuilder.TermSpec(
                                x.budgetedAmount,
                                x.startDate,
                                x.endDate
                        ))
                        .collect(Collectors.toList())))
                ;
    }

    @Override
    public Project findUsing(final ServiceRegistry2 serviceRegistry) {
        final ProjectRepository projectRepository = serviceRegistry.lookupService(ProjectRepository.class);
        return projectRepository.findByReference(ref);
    }
}
