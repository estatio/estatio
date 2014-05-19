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
package org.estatio.fixture;

import org.estatio.fixture.asset.registration.refdata.FixedAssetRegistrationTypeForItalyRefData;
import org.estatio.fixture.lease.refdata.LeaseTypeForItalyRefData;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

public class EstatioRefDataForItalySetupFixture extends CompositeFixtureScript {

    public EstatioRefDataForItalySetupFixture() {
        super(null, "ref-data");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute("fixed-asset-refdata-italy", new FixedAssetRegistrationTypeForItalyRefData(), executionContext);
        execute("lease-refdata-italy", new LeaseTypeForItalyRefData(), executionContext);
    }
}
