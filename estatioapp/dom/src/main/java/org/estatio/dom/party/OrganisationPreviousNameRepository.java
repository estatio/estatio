/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.party;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = OrganisationPreviousName.class, nature = NatureOfService.DOMAIN)
public class OrganisationPreviousNameRepository extends UdoDomainRepositoryAndFactory<OrganisationPreviousName> {

    public OrganisationPreviousNameRepository() {
        super(OrganisationPreviousNameRepository.class, OrganisationPreviousName.class);
    }

    // //////////////////////////////////////

    public OrganisationPreviousName newOrganisationPreviousName(
            final String name,
            final LocalDate endDate) {
        OrganisationPreviousName organisationPreviousName = newTransientInstance(OrganisationPreviousName.class);
        organisationPreviousName.setName(name);
        organisationPreviousName.setEndDate(endDate);
        persistIfNotAlready(organisationPreviousName);
        return organisationPreviousName;
    }
}
