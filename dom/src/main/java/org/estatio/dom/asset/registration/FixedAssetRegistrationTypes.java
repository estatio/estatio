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
package org.estatio.dom.asset.registration;

import org.estatio.dom.EstatioDomainService;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;

@Hidden
public class FixedAssetRegistrationTypes extends EstatioDomainService<FixedAssetRegistrationType> {

    public FixedAssetRegistrationTypes() {
        super(FixedAssetRegistrationTypes.class, FixedAssetRegistrationType.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public FixedAssetRegistrationType create(String title, Class<? extends FixedAssetRegistration> cls) {
        FixedAssetRegistrationType fixedAssetRegistrationType = newTransientInstance(FixedAssetRegistrationType.class);
        fixedAssetRegistrationType.setTitle(title);
        fixedAssetRegistrationType.setFullyQualifiedClassName(cls.getName());
        persist(fixedAssetRegistrationType);
        return fixedAssetRegistrationType;
    }

    // //////////////////////////////////////

    @Programmatic
    public FixedAssetRegistrationType findByTitle(final String title) {
        return firstMatch("findByTitle", "title", title);
    }

    // //////////////////////////////////////

    @Programmatic
    public FixedAssetRegistrationType findOrCreate(String title, Class<? extends FixedAssetRegistration> cls) {
        final FixedAssetRegistrationType type = findByTitle(title);
        if (type != null) {
            return type;
        }
        return create(title, cls);
    }

}
