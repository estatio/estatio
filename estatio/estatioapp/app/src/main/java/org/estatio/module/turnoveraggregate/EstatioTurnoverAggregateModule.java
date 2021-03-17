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
package org.estatio.module.turnoveraggregate;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.turnover.EstatioTurnoverModule;
import org.estatio.module.turnoveraggregate.dom.PurchaseCountAggregateForPeriod;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregateForPeriod;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregateToDate;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;

@XmlRootElement(name = "module")
public final class EstatioTurnoverAggregateModule extends ModuleAbstract {

    public EstatioTurnoverAggregateModule(){}

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new EstatioTurnoverModule());
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                deleteFrom(TurnoverAggregation.class);
                deleteFrom(TurnoverAggregateForPeriod.class);
                deleteFrom(TurnoverAggregateToDate.class);
                deleteFrom(PurchaseCountAggregateForPeriod.class);
            }
        };
    }
}
