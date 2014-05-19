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
package org.estatio.fixture.lease;

import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

// unused
class LeasesEtcForAll extends CompositeFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        execute(new LeasesEtcForOxfTopModel001(), executionContext);
        execute(new LeasesEtcForOxfMediax002(), executionContext);
        execute(new LeasesEtcForOxfPoison003(), executionContext);
        execute(new LeasesEtcForOxfPret004(), executionContext);
        execute(new LeasesEtcForOxfMiracl005(), executionContext);
        execute(new LeasesEtcForKalPoison001(), executionContext);
    }

}
