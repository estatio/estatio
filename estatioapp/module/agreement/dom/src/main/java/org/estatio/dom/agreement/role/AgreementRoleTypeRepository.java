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
package org.estatio.dom.agreement.role;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.IAgreementType;
import org.estatio.dom.agreement.AgreementTypeRepository;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AgreementRoleType.class
)
public class AgreementRoleTypeRepository extends UdoDomainRepositoryAndFactory<AgreementRoleType> {

    public AgreementRoleTypeRepository() {
        super(AgreementRoleTypeRepository.class, AgreementRoleType.class);
    }

    public AgreementRoleType find(final IAgreementRoleType data) {
        return findByTitle(data.getTitle());
    }

    @Deprecated
    public AgreementRoleType findByTitle(final String title) {
        return queryResultsCache.execute(new Callable<AgreementRoleType>() {
            @Override
            public AgreementRoleType call() throws Exception {
                return firstMatch("findByTitle", "title", title);
            }
        }, AgreementRoleTypeRepository.class, "findByTitle", title);
    }

    public List<AgreementRoleType> findApplicableTo(final AgreementType agreementType) {
        return queryResultsCache.execute(new Callable<List<AgreementRoleType>>() {
            @Override
            public List<AgreementRoleType> call() throws Exception {
                return allMatches("findByAgreementType", "agreementType", agreementType);
            }
        }, AgreementRoleTypeRepository.class, "findApplicableTo", agreementType);
    }

    public AgreementRoleType findByAgreementTypeAndTitle(final AgreementType agreementType, final String title) {
        return queryResultsCache.execute(new Callable<AgreementRoleType>() {
            @Override
            public AgreementRoleType call() throws Exception {
                return firstMatch("findByAgreementTypeAndTitle", "agreementType", agreementType, "title", title);
            }
        }, AgreementRoleTypeRepository.class, "findByAgreementTypeAndTitle", agreementType, title);
    }

    public AgreementRoleType findOrCreate(final IAgreementRoleType data, final IAgreementType appliesTo) {
        final AgreementType agreementType = agreementTypeRepository.find(appliesTo);
        return findOrCreate(data.getTitle(), agreementType);
    }

    @Deprecated
    public AgreementRoleType findOrCreate(final String title, final AgreementType appliesTo) {
        AgreementRoleType agreementRoleType = findByAgreementTypeAndTitle(appliesTo, title);
        if (agreementRoleType == null) {
            agreementRoleType = getContainer().newTransientInstance(AgreementRoleType.class);
            agreementRoleType.setTitle(title);
            agreementRoleType.setAppliesTo(appliesTo);
            getContainer().persist(agreementRoleType);
        }
        return agreementRoleType;
    }

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    QueryResultsCache queryResultsCache;
}
