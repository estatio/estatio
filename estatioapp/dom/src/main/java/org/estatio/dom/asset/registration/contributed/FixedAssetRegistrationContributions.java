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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.FixedAssetRegistrationRepository;
import org.estatio.dom.asset.registration.FixedAssetRegistrationType;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY,
        menuOrder = "10"
)
public class FixedAssetRegistrationContributions extends UdoDomainService<FixedAssetRegistrationContributions> {

    public FixedAssetRegistrationContributions() {
        super(FixedAssetRegistrationContributions.class);
    }

    @Action(
            semantics =  SemanticsOf.NON_IDEMPOTENT
    )
    @MemberOrder(
            name = "Registrations",
            sequence = "13"
    )
    public FixedAssetRegistration newRegistration(
            final FixedAsset subject,
            final FixedAssetRegistrationType type) {
        final FixedAssetRegistration registration = type.create(getContainer());
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

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @MemberOrder(
            name = "Registrations",
            sequence = "13.5"
    )
    public List<FixedAssetRegistration> registrations(final FixedAsset subject) {
        return fixedAssetRegistrationRepository.findBySubject(subject);
    }

    // //////////////////////////////////////

    @Inject
    FixedAssetRegistrationRepository fixedAssetRegistrationRepository;

}
