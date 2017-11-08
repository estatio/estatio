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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.asset.dom.FixedAsset;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = FixedAssetRegistration.class
)
public class FixedAssetRegistrationRepository extends UdoDomainRepositoryAndFactory<FixedAssetRegistration> {

    public FixedAssetRegistrationRepository() {
        super(FixedAssetRegistrationRepository.class, FixedAssetRegistration.class);
    }

    public List<FixedAssetRegistration> findBySubject(
            final FixedAsset asset) {
        return allMatches("findBySubject",
                "subject", asset);
    }

    public List<FixedAssetRegistration> findBySubjectAndType(
            final FixedAsset asset,
            final FixedAssetRegistrationType type) {
        return allMatches("findBySubject",
                "subject", asset,
                "type", type);
    }

    public List<FixedAssetRegistration> allRegistrations() {
        return allInstances();
    }

}
