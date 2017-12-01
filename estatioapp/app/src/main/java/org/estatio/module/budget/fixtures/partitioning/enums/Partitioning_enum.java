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

package org.estatio.module.budget.fixtures.partitioning.enums;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.dom.partioning.PartitioningRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum;
import org.estatio.module.budget.fixtures.partitioning.builders.PartitioningBuilder;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;

//@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Partitioning_enum
        implements PersonaWithBuilderScript<Partitioning, PartitioningBuilder>,
        PersonaWithFinder<Partitioning> {

    OxfBudget2015(
            Budget_enum.OxfBudget2015, BudgetCalculationType.BUDGETED,
            new ItemSpec[]{
                new ItemSpec(Charge_enum.GbServiceCharge, KeyTable_enum.Oxf2015Area, Budget_enum.OxfBudget2015.getCharge1_d(), bd(100)),
                new ItemSpec(Charge_enum.GbServiceCharge, KeyTable_enum.Oxf2015Area, Budget_enum.OxfBudget2015.getCharge2_d(), bd(80)),
                new ItemSpec(Charge_enum.GbServiceCharge, KeyTable_enum.Oxf2015Count, Budget_enum.OxfBudget2015.getCharge2_d(), bd(20)),
            })
    ;

    private final Budget_enum budget_d;
    private final BudgetCalculationType budgetCalculationType;
    private final ItemSpec[] itemSpecs;

    @AllArgsConstructor
    @Data
    public static class ItemSpec {
        private final Charge_enum charge_d;
        private final KeyTable_enum keyTable_d;
        private final Charge_enum itemCharge_d;
        private final BigDecimal percentage;
    }

    Partitioning_enum(
            final Budget_enum budget_d,
            final BudgetCalculationType budgetCalculationType,
            final ItemSpec[] itemSpecs) {
        this.budget_d = budget_d;
        this.budgetCalculationType = budgetCalculationType;
        this.itemSpecs = itemSpecs;
    }

    @Override
    public Partitioning findUsing(final ServiceRegistry2 serviceRegistry) {
        final Budget budget = budget_d.findUsing(serviceRegistry);
        return serviceRegistry.lookupService(PartitioningRepository.class).findUnique(budget, budgetCalculationType, budget.getStartDate());
    }

    @Override
    public PartitioningBuilder toBuilderScript() {
        return new PartitioningBuilder()
                .setPrereq((f, ec) -> {
                    final Budget budget = f.objectFor(budget_d, ec);
                    f.setBudget(budget);
                    f.setStartDate(budget.getStartDate());
                })
                .setPrereq((f,ec) -> f.setItemSpec(
                        Arrays.stream(itemSpecs).map(x ->
                                new PartitioningBuilder.ItemSpec(
                                        f.objectFor(x.charge_d, ec),
                                        f.objectFor(x.keyTable_d, ec),
                                        f.objectFor(x.itemCharge_d, ec),
                                        x.percentage)
                        ).collect(Collectors.toList())
                ));
    }

}
