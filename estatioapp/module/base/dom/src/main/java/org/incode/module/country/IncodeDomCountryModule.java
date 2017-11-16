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
package org.incode.module.country;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.CountryModule;
import org.incode.module.country.dom.impl.State;
import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.base.platform.applib.ModuleAbstract;
import org.estatio.module.base.platform.fixturesupport.DemoData2Persist;
import org.estatio.module.base.platform.fixturesupport.DemoData2Teardown;
import org.estatio.module.country.fixtures.enums.Country_enum;

/**
 * This is a "proxy" for the corresponding module defined in the Incode Platform.
 */
@XmlRootElement(name = "module")
public final class IncodeDomCountryModule extends ModuleAbstract {

    @Override
    public Set<Class<?>> getDependenciesAsClass() {
        return Sets.newHashSet(CountryModule.class);
    }

    @Override
    public FixtureScript getRefDataSetupFixture() {
        return new DemoData2Persist<>(Country_enum.class);
    }

    @Override
    public FixtureScript getTeardownFixture() {
        final TeardownFixtureAbstract teardownState = new TeardownFixtureAbstract() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                deleteFrom(State.class);
            }
        };
        return Utils.allOf(teardownState, new DemoData2Teardown<>(Country_enum.class));
    }



}
