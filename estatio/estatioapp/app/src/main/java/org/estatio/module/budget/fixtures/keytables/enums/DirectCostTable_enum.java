/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.module.budget.fixtures.keytables.enums;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.builders.DirectCostTableBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum DirectCostTable_enum implements PersonaWithBuilderScript<DirectCostTable, DirectCostTableBuilder>, PersonaWithFinder<DirectCostTable> {

    Oxf2015Direct(
            Budget_enum.OxfBudget2015, "Direct costs year 2015", Arrays.asList(bd("100.00"), bd("123.45"), null, bd("543.21"))
    ),
    ;

    private final Budget_enum budget_d;
    private final String name;
    private final List<BigDecimal> values;

    public LocalDate getStartDate() {
        return budget_d.getStartDate();
    }

    @Override
    public DirectCostTableBuilder builder() {
        return new DirectCostTableBuilder()
                .setPrereq((f,ec) -> f.setBudget(f.objectFor(budget_d, ec)))
                .setName(name)
                .setValues(values)
                ;
    }

    @Override
    public DirectCostTable findUsing(final ServiceRegistry2 serviceRegistry) {

        final Budget budget = budget_d.findUsing(serviceRegistry);
        final PartitioningTableRepository partitioningTableRepository = serviceRegistry.lookupService(PartitioningTableRepository.class);

        return (DirectCostTable) partitioningTableRepository.findByBudgetAndName(budget, name);
    }
}
