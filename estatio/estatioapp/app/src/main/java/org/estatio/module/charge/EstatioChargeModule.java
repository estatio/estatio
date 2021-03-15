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
package org.estatio.module.charge;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.fixtures.chargegroups.enums.ChargeGroup_enum;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.countryapptenancy.EstatioCountryAppTenancyModule;
import org.estatio.module.tax.EstatioTaxModule;

@XmlRootElement(name = "module")
public final class EstatioChargeModule extends ModuleAbstract {

    public EstatioChargeModule(){}

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new EstatioTaxModule(), new EstatioCountryAppTenancyModule());
    }

    private static final ThreadLocal<Boolean> refData = ThreadLocal.withInitial(() -> false);
    @Override
    public FixtureScript getRefDataSetupFixture() {
        if(refData.get()) {
            return null;
        }
        // else
        refData.set(true);
        return new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new ChargeGroup_enum.PersistAll());
                executionContext.executeChild(this, new Charge_enum.PersistAll());
            }
        };
    }

    @Override
    public FixtureScript getTeardownFixture() {
        // leave reference data alone
        return null;
    }

    /**
     * Provided for any integration tests that need to fine-tune
     */
    public FixtureScript getRefDataTeardown() {
        refData.set(false); // reset
        return new TeardownFixtureAbstract() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                deleteFrom(Charge.class);
                deleteFrom(ChargeGroup.class);
            }
        };
    }

}
