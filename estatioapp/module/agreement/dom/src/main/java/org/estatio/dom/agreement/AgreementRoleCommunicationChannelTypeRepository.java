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
package org.estatio.dom.agreement;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AgreementRoleCommunicationChannelType.class
)
public class AgreementRoleCommunicationChannelTypeRepository
        extends UdoDomainRepositoryAndFactory<AgreementRoleCommunicationChannelType> {

    public AgreementRoleCommunicationChannelTypeRepository() {
        super(AgreementRoleCommunicationChannelTypeRepository.class, AgreementRoleCommunicationChannelType.class);
    }

    public AgreementRoleCommunicationChannelType find(final AgreementRoleCommunicationChannelTypeData data) {
        return findByTitle(data.getTitle());
    }

    @Deprecated
    public AgreementRoleCommunicationChannelType findByTitle(final String title) {
        return queryResultsCache.execute(new Callable<AgreementRoleCommunicationChannelType>() {
            @Override
            public AgreementRoleCommunicationChannelType call() throws Exception {
                return firstMatch("findByTitle", "title", title);
            }
        }, AgreementRoleCommunicationChannelTypeRepository.class, "findByTitle", title);
    }

    public AgreementRoleCommunicationChannelType findByAgreementTypeAndTitle(final AgreementType agreementType, final String title) {
        return queryResultsCache.execute(new Callable<AgreementRoleCommunicationChannelType>() {
            @Override
            public AgreementRoleCommunicationChannelType call() throws Exception {
                return firstMatch("findByAgreementTypeAndTitle", "agreementType", agreementType, "title", title);
            }
        }, AgreementRoleCommunicationChannelTypeRepository.class, "findByAgreementTypeAndTitle", agreementType, title);
    }

    public List<AgreementRoleCommunicationChannelType> findApplicableTo(final AgreementType agreementType) {
        return queryResultsCache.execute(new Callable<List<AgreementRoleCommunicationChannelType>>() {
            @Override
            public List<AgreementRoleCommunicationChannelType> call() throws Exception {
                return allMatches("findByAgreementType", "agreementType", agreementType);
            }
        }, AgreementRoleCommunicationChannelTypeRepository.class, "findApplicableTo", agreementType);
    }

    public AgreementRoleCommunicationChannelType findOrCreate(final AgreementRoleCommunicationChannelTypeData data, final IAgreementType IAgreementType) {
        final String title = data.getTitle();
        final AgreementType agreementType = agreementTypeRepository.find(IAgreementType);
        AgreementRoleCommunicationChannelType arcct = findByAgreementTypeAndTitle(agreementType, title);
        if (arcct == null) {
            arcct = getContainer().newTransientInstance(AgreementRoleCommunicationChannelType.class);
            arcct.setTitle(title);
            arcct.setAppliesTo(agreementType);
            getContainer().persist(arcct);
        }
        return arcct;
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    AgreementTypeRepository agreementTypeRepository;


}
