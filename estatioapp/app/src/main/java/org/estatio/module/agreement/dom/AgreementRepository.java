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
package org.estatio.module.agreement.dom;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Agreement.class
)
public class AgreementRepository extends UdoDomainRepositoryAndFactory<Agreement> {

    public AgreementRepository() {
        super(AgreementRepository.class, Agreement.class);
    }

    // //////////////////////////////////////

    public Agreement findAgreementByTypeAndReference(final AgreementType agreementType, final String reference) {
        List<Agreement> list = repositoryService.allMatches(new QueryDefault<>(Agreement.class,"findByTypeAndReference",
                "agreementType", agreementType,
                "reference", reference));
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Agreement> findByTypeAndReferenceOrName(
            final AgreementType agreementType,
            final String regex) {
        return repositoryService.allMatches(new QueryDefault<>(Agreement.class,"findByTypeAndReferenceOrName",
                "agreementType", agreementType,
                "regex", regex));
    }

    public List<Agreement> findByAgreementTypeAndRoleTypeAndParty(
            final AgreementType agreementType,
            final AgreementRoleType agreementRoleType,
            final Party party) {
        return repositoryService.allMatches(new QueryDefault<>(Agreement.class,"findByAgreementTypeAndRoleTypeAndParty",
                "agreementType", agreementType,
                "roleType", agreementRoleType,
                "party", party));
    }

    public List<Agreement> findByTypeTitleAndReferenceOrName(
            final String titleOfAgreementType,
            final String regex) {
        final AgreementType agreementType = agreementTypeRepository.find(titleOfAgreementType);
        return findByTypeAndReferenceOrName(agreementType, regex);
    }

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @javax.inject.Inject
    public RepositoryService repositoryService;

}
