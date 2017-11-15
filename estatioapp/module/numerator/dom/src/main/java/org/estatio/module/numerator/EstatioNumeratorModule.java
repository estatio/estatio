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
package org.estatio.module.numerator;

import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.base.EstatioBaseModule;
import org.estatio.module.base.platform.applib.Module;
import org.estatio.module.base.platform.applib.ModuleAbstract;
import org.estatio.module.base.platform.fixturesupport.DemoData2Persist;
import org.estatio.module.base.platform.fixturesupport.DemoData2Teardown;
import org.estatio.module.numerator.fixtures.data.NumeratorExampleObject;
import org.estatio.module.numerator.fixtures.data.NumeratorExampleObject_enum;

public class EstatioNumeratorModule extends ModuleAbstract {


    public EstatioNumeratorModule() {}

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new EstatioBaseModule());
    }

    @Override
    public FixtureScript getRefDataSetupFixture() {
        return new DemoData2Persist<NumeratorExampleObject_enum, NumeratorExampleObject>(NumeratorExampleObject_enum.class) {};
    }

    @Override
    public FixtureScript getTeardownFixture(){
        return new DemoData2Teardown<NumeratorExampleObject_enum, NumeratorExampleObject>(NumeratorExampleObject_enum.class) {};
    }


    public abstract static class ActionDomainEvent<S>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }

    public abstract static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }

    public abstract static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }

}

