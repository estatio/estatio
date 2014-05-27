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
package org.estatio.fixture.asset.registration.refdata;


import org.estatio.dom.asset.registration.FixedAssetRegistrationType;
import org.estatio.dom.asset.registration.LandRegister;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class FixedAssetRegistrationTypeForItalyRefData extends FixtureScript {

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        createFixedAssetRegistrationType("LandRegister", fixtureResults);
    }

    private void createFixedAssetRegistrationType(String title, ExecutionContext fixtureResults) {

        final FixedAssetRegistrationType farType = getContainer().newTransientInstance(FixedAssetRegistrationType.class);
        farType.setTitle(title);
        farType.setFullyQualifiedClassName(LandRegister.class.getName());
        getContainer().persist(farType);

        fixtureResults.add(this, farType.getTitle(), farType);
    }

}
