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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = FixedAssetRegistrationType.class
)
public class FixedAssetRegistrationTypeRepository extends UdoDomainRepositoryAndFactory<FixedAssetRegistrationType> {

    public FixedAssetRegistrationTypeRepository() {
        super(FixedAssetRegistrationTypeRepository.class, FixedAssetRegistrationType.class);
    }

    public FixedAssetRegistrationType create(String title, Class<? extends FixedAssetRegistration> cls) {
        FixedAssetRegistrationType fixedAssetRegistrationType = newTransientInstance(FixedAssetRegistrationType.class);
        fixedAssetRegistrationType.setTitle(title);
        fixedAssetRegistrationType.setFullyQualifiedClassName(cls.getName());
        persist(fixedAssetRegistrationType);
        return fixedAssetRegistrationType;
    }

    public FixedAssetRegistrationType findByTitle(final String title) {
        return firstMatch("findByTitle", "title", title);
    }

    public FixedAssetRegistrationType findOrCreate(String title, Class<? extends FixedAssetRegistration> cls) {
        final FixedAssetRegistrationType type = findByTitle(title);
        if (type != null) {
            return type;
        }
        return create(title, cls);
    }

}
