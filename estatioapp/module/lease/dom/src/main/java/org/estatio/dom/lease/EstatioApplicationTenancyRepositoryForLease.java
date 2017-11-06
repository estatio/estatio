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
package org.estatio.dom.lease;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.asset.EstatioApplicationTenancyRepositoryForProperty;
import org.estatio.dom.asset.Property;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioApplicationTenancyRepositoryForLease {


    public ApplicationTenancy findOrCreateTenancyFor(final Lease lease) {
        return estatioApplicationTenancyRepositoryForProperty.findOrCreateTenancyFor(lease.getProperty());
    }

    public ApplicationTenancy findOrCreateTenancyFor(final LeaseItem leaseItem) {
        return findOrCreateTenancyFor(leaseItem.getLease().getProperty(), leaseItem.getLease().getPrimaryParty());
    }

    public ApplicationTenancy findOrCreateTenancyFor(final Property property, final Party party) {
        ApplicationTenancy propertyPartyTenancy = applicationTenancies.findByPath(pathFor(property, party));
        if (propertyPartyTenancy != null){
            return propertyPartyTenancy;
        }
        final ApplicationTenancy propertyApplicationTenancy = estatioApplicationTenancyRepositoryForProperty.findOrCreateTenancyFor(property);
        final String tenancyName = String.format("%s/%s ", propertyApplicationTenancy.getPath(), party.getReference());
        return applicationTenancies.newTenancy(tenancyName, pathFor(property,party), propertyApplicationTenancy);
    }


    protected String pathFor(final Property property, final Party party) {
        return estatioApplicationTenancyRepositoryForProperty.pathFor(property).concat(String.format("/%s", party.getReference()));
    }



    @Inject
    ApplicationTenancyRepository applicationTenancies;

    @Inject
    EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepositoryForProperty;



}
