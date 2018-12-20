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

package org.estatio.module.budget.fixtures.keytables.builders;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.DirectCostTableRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"budget", "name"}, callSuper = false)
@ToString(of={"budget", "name"})
@Accessors(chain = true)
public class DirectCostTableBuilder extends BuilderScriptAbstract<DirectCostTable, DirectCostTableBuilder> {

    @Getter @Setter
    Budget budget;
    @Getter @Setter
    String name;
    @Getter @Setter
    List<BigDecimal> values;

    @Getter
    DirectCostTable object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("budget", executionContext, Budget.class);
        checkParam("name", executionContext, String.class);
        checkParam("values", executionContext, List.class);


        final DirectCostTable directCostTable =
                directCostTableRepository.newDirectCostTable(
                        budget, name);
        directCostTable.generateItems();
        transactionService.nextTransaction();
        Iterator<DirectCost> i = directCostTable.getItems().iterator();
        for (BigDecimal value : getValues()){
            if (!i.hasNext()) break;
            i.next().setBudgetedValue(value);
        }

        executionContext.addResult(this, name, directCostTable);

        object = directCostTable;
    }

    @Inject
    DirectCostTableRepository directCostTableRepository;

}
