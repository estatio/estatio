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

package org.estatio.module.budget.fixtures.keytables.personas;

import org.estatio.module.budget.fixtures.keytables.KeyTableAbstract;
import org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum;

public class KeyTablesForBud extends KeyTableAbstract {

//    public static final String NAME_BY_AREA = KeyTable_enum.Bud2015Area.getName();
//    public static final String NAME_BY_COUNT = KeyTable_enum.Bud2015Count.getName();

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChildT(this, KeyTable_enum.Bud2015Area.toBuilderScript());
        executionContext.executeChildT(this, KeyTable_enum.Bud2015Count.toBuilderScript());

//        // prereqs
//        Budget budget = Budget_enum.BudBudget2015.toBuilderScript().build(this, executionContext).getObject();
//
//        // exec
//        createKeyTable(
//                budget,
//                NAME_BY_AREA,
//                FoundationValueType.AREA,
//                KeyValueMethod.PROMILLE, 6,
//                executionContext);
//        createKeyTable(
//                budget,
//                NAME_BY_COUNT,
//                FoundationValueType.COUNT,
//                KeyValueMethod.PROMILLE, 6,
//                executionContext);
    }
}
