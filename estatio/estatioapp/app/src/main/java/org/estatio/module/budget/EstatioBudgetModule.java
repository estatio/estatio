/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.module.budget;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.asset.EstatioAssetModule;
import org.estatio.module.base.EstatioBaseModule;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.budgetitem.BudgetItemValue;
import org.estatio.module.budget.dom.keyitem.PartitioningTableItem;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.charge.EstatioChargeModule;

@XmlRootElement(name = "module")
public final class EstatioBudgetModule extends ModuleAbstract {

    public EstatioBudgetModule(){}

    @Override
    public Set<Module> getDependencies(){
        return Sets.newHashSet(new EstatioAssetModule(), new EstatioChargeModule(), new EstatioBaseModule());
    }


    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                deleteFrom(BudgetCalculation.class);
                deleteFrom(PartitionItem.class);
                deleteFrom(Partitioning.class);
                deleteFrom(BudgetItemValue.class);
                deleteFrom(BudgetItem.class);
                deleteFrom(PartitioningTableItem.class);
                deleteFrom(PartitioningTable.class);
                deleteFrom(Budget.class);
            }
        };
    }



}
