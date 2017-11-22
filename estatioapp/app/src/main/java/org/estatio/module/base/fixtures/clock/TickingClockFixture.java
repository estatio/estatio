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
package org.estatio.module.base.fixtures.clock;

import org.apache.isis.applib.clock.Clock;
import org.apache.isis.applib.fixtures.FixtureClock;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.fixturesupport.dom.scripts.ClockFixture;

import org.estatio.module.base.platform.applib.TickingFixtureClock;

public class TickingClockFixture extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        final Clock instance = Clock.getInstance();

        if(instance instanceof TickingFixtureClock) {
            TickingFixtureClock.reinstateExisting();
            executionContext.executeChild(this, ClockFixture.setTo("2014-05-18"));
            TickingFixtureClock.replaceExisting();
        }

        if(instance instanceof FixtureClock) {
            executionContext.executeChild(this, ClockFixture.setTo("2014-05-18"));
        }
    }

}
