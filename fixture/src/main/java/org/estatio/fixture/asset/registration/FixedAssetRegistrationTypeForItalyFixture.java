/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.fixture.asset.registration;


import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.fixtures.AbstractFixture;

import org.estatio.dom.asset.registration.LandRegister;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.FixedAssetRegistrationType;

public class FixedAssetRegistrationTypeForItalyFixture extends AbstractFixture {

    @Override
    public void install() {
        createFixedAssetRegistrationType("LandRegister", LandRegister.class, getContainer());
    }

    private static FixedAssetRegistrationType createFixedAssetRegistrationType(
            final String title, 
            final Class<? extends FixedAssetRegistration> cls, 
            final DomainObjectContainer container) {
        final FixedAssetRegistrationType farType = container.newTransientInstance(FixedAssetRegistrationType.class);
        farType.setTitle(title);
        farType.setFullyQualifiedClassName(cls.getName());
        container.persist(farType);
        return farType;
    }

}
