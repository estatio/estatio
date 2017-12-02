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
import static org.estatio.module.budget.fixtures.budgets.enums.Budget_enum.BudBudget2015;
import static org.estatio.module.budget.fixtures.budgets.enums.Budget_enum.OxfBudget2015;
import static org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum.*;
import static org.estatio.module.charge.fixtures.charges.enums.Charge_enum.*;
import static org.incode.module.base.integtests.VT.bd;

//@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Partitioning_enum
        implements PersonaWithBuilderScript<Partitioning, PartitioningBuilder>,
        PersonaWithFinder<Partitioning> {

    OxfPartitioning2015(
            OxfBudget2015, BudgetCalculationType.BUDGETED,
            new ItemSpec[]{
                new ItemSpec(OxfBudget2015, GbServiceCharge, Oxf2015Area, 0, bd(100)),
                new ItemSpec(OxfBudget2015, GbServiceCharge, Oxf2015Area, 1, bd(80)),
                new ItemSpec(OxfBudget2015, GbServiceCharge, Oxf2015Count, 1, bd(20)),
            }),
    BudPartitioning2015(
            BudBudget2015, BudgetCalculationType.BUDGETED,
            new ItemSpec[]{
                new ItemSpec(BudBudget2015, NlServiceCharge, Bud2015Area, 0, bd(100)),
                new ItemSpec(BudBudget2015, NlServiceCharge, Bud2015Area, 1, bd(80)),
                new ItemSpec(BudBudget2015, NlServiceCharge, Bud2015Count, 1, bd(20)),
                new ItemSpec(BudBudget2015, NlServiceCharge2, Bud2015Area, 2, bd(90)),
                new ItemSpec(BudBudget2015, NlServiceCharge, Bud2015Count, 2, bd(10)),
            }),
    ;

    private final Budget_enum budget_d;
    private final BudgetCalculationType budgetCalculationType;
    private final ItemSpec[] itemSpecs;

    @AllArgsConstructor
    @Data
    public static class ItemSpec {
        private final Budget_enum budget_d;
        private final Charge_enum charge_d;
        private final KeyTable_enum keyTable_d;
        private final int itemIndex;
        private final BigDecimal percentage;

        public Charge_enum getItemCharge_d() {
            return budget_d.getItemSpecs()[itemIndex].getCharge_d();
        }
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
    public PartitioningBuilder builder() {
        return new PartitioningBuilder()
                .setPrereq((f, ec) -> {
                    final Budget budget = f.objectFor(budget_d, ec);
                    f.setBudget(budget);
                    f.setStartDate(budget.getStartDate());
                })
                .setPrereq((f,ec) -> f.setItemSpec(
                        Arrays.stream(itemSpecs).map(x ->
                                new PartitioningBuilder.ItemSpec(
                                        f.objectFor(x.getItemCharge_d(), ec), f.objectFor(x.keyTable_d, ec),
                                        f.objectFor(x.charge_d, ec),
                                        x.percentage)
                        ).collect(Collectors.toList())
                ));
    }

}
