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
package org.estatio.numerator.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.integtestsupport.dom.IncodeFixtureAbstract;

import org.incode.module.integtestsupport.dom.ClockFixture;
import org.estatio.numerator.fixture.dom.NumeratorExampleObject;

import lombok.Getter;

public class NumeratorModuleBaseLineFixture extends DiscoverableFixtureScript {

    public NumeratorModuleBaseLineFixture() {
        super(null, "baseline");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChild(this, ClockFixture.setTo("2014-05-18"));
        executionContext.executeChild(this, new NumeratorModuleTeardownFixture());
        executionContext.executeChild(this, new NumeratorModuleTeardownFixture());

        final CreateNumeratorExampleObjects fs = new CreateNumeratorExampleObjects();
        executionContext.executeChild(this, fs);

        foo = fs.getFoo();
        bar = fs.getBar();
    }

    @Getter
    NumeratorExampleObject foo;
    @Getter
    NumeratorExampleObject bar;

    private class CreateNumeratorExampleObjects extends IncodeFixtureAbstract {
        @Override
        protected void execute(final ExecutionContext executionContext) {
            foo = create("Foo");
            bar = create("Bar");
        }

        private NumeratorExampleObject create(final String name) {
            final NumeratorExampleObject obj = new NumeratorExampleObject(name);
            repositoryService.persist(obj);
            return obj;
        }

        @Inject
        protected RepositoryService repositoryService;

        @Getter
        NumeratorExampleObject foo;
        @Getter
        NumeratorExampleObject bar;

    }
}
