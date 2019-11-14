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

import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

import javax.inject.Inject;
import java.util.List;

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
        List<AgreementType> list = repositoryService.allMatches(new QueryDefault<>(AgreementType.class,
                "findByTitle", "title", title));
        return list.isEmpty() ? null : list.get(0);
    }

    public AgreementType findOrCreate(final IAgreementType data) {
        return findOrCreate(data.getTitle());
    }

    @Deprecated
    public AgreementType findOrCreate(final String title) {
        AgreementType agreementType = find(title);
        if (agreementType == null) {
            agreementType = factoryService.instantiate(AgreementType.class);
            agreementType.setTitle(title);
            repositoryService.persist(agreementType);
        }
        return agreementType;
    }

    @javax.inject.Inject
    public RepositoryService repositoryService;

    @Inject
    public FactoryService factoryService;
}
