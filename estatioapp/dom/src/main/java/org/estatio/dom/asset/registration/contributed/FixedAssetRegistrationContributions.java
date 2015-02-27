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
package org.estatio.dom.asset.registration.contributed;

import java.util.List;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.NotContributed.As;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.FixedAssetRegistrationType;
import org.estatio.dom.asset.registration.FixedAssetRegistrations;

@DomainService(menuOrder = "10")
@Hidden
public class FixedAssetRegistrationContributions extends UdoDomainService<FixedAssetRegistrationContributions> {

    public FixedAssetRegistrationContributions() {
        super(FixedAssetRegistrationContributions.class);
    }

    @NotInServiceMenu
    @MemberOrder(name = "Registrations", sequence = "13")
    @ActionSemantics(Of.NON_IDEMPOTENT)
    public FixedAssetRegistration newRegistration(
            final FixedAsset subject,
            final @Named("Type") FixedAssetRegistrationType registrationType) {
        final FixedAssetRegistration registration = registrationType.create(getContainer());
        registration.setSubject(subject);
        persistIfNotAlready(registration);
        return registration;
    }

    public boolean hideNewRegistration(
            final FixedAsset subject,
            final FixedAssetRegistrationType registrationType) {
        return false; // TODO: return true if action is hidden, false if visible
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @NotContributed(As.ACTION)
    @ActionSemantics(Of.SAFE)
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
