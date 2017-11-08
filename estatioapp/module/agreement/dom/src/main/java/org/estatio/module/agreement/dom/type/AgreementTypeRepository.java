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
package org.estatio.module.agreement.dom.type;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AgreementType.class
)
public class AgreementTypeRepository extends UdoDomainRepositoryAndFactory<AgreementType> {

    public AgreementTypeRepository() {
        super(AgreementTypeRepository.class, AgreementType.class);
    }

    public AgreementType find (final IAgreementType data){
        return find(data.getTitle());
    }

    @Deprecated
    public AgreementType find(final String title) {
        return firstMatch("findByTitle", "title", title);
    }

    public AgreementType findOrCreate(final IAgreementType data) {
        return findOrCreate(data.getTitle());
    }

    @Deprecated
    public AgreementType findOrCreate(final String title) {
        AgreementType agreementType = find(title);
        if (agreementType == null) {
            agreementType = getContainer().newTransientInstance(AgreementType.class);
            agreementType.setTitle(title);
            getContainer().persist(agreementType);
        }
        return agreementType;
    }

}
