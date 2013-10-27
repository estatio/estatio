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
package org.estatio.dom.asset.registration.contributed;

import java.util.List;

import org.apache.isis.applib.AbstractContainedObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.FixedAssetRegistrationType;
import org.estatio.dom.asset.registration.FixedAssetRegistrations;

@Hidden
public class FixedAssetRegistrationContributions extends AbstractContainedObject {

    @NotInServiceMenu
    @MemberOrder(name = "Registrations", sequence = "13")
    public FixedAssetRegistration newRegistration(
            final FixedAsset subject, 
            final @Named("Type") FixedAssetRegistrationType registrationType) {
        final FixedAssetRegistration registration = registrationType.create(getContainer());
        registration.setSubject(subject);
        persistIfNotAlready(registration);
        return registration;
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @NotContributed(As.ACTION)
    @MemberOrder(name = "Registrations", sequence = "13.5")
    public List<FixedAssetRegistration> registrations(final FixedAsset subject) {
        return fixedAssetRegistrations.findBySubject(subject);
    }

    // //////////////////////////////////////
    
    private FixedAssetRegistrations fixedAssetRegistrations;
    
    public void injectFixedAssetRegistrations(final FixedAssetRegistrations fixedAssetRegistrations) {
        this.fixedAssetRegistrations = fixedAssetRegistrations;
    }

}
