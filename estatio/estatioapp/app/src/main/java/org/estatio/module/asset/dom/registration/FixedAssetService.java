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
package org.estatio.module.asset.dom.registration;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.asset.dom.FixedAsset;

// TODO: REVIEW - why not just a simple derived property since these are in the same module?
@DomainService(
        nature = NatureOfService.DOMAIN,
        menuOrder = "10"
)
public class FixedAssetService extends UdoDomainService<FixedAssetService> {

    public FixedAssetService() {
        super(FixedAssetService.class);
    }

    public FixedAssetRegistration newRegistration(
            final FixedAsset subject,
            final FixedAssetRegistrationType type) {
        final FixedAssetRegistration registration = type.create(factoryService);
        registration.setSubject(subject);
        repositoryService.persist(registration);
        return registration;
    }


    // //////////////////////////////////////

    public List<FixedAssetRegistration> registrations(final FixedAsset subject) {
        return fixedAssetRegistrationRepository.findBySubject(subject);
    }

    // //////////////////////////////////////

    @Inject
    FixedAssetRegistrationRepository fixedAssetRegistrationRepository;

    @Inject
    FactoryService factoryService;
    @Inject
    RepositoryService repositoryService;
}
