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
package org.estatio.module.lease.dom.occupancy.salesarea;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.party.dom.Party;

@DomainService(
    nature = NatureOfService.DOMAIN,
    repositoryFor = SalesAreaLicense.class
)
public class SalesAreaLicenseRepository extends UdoDomainRepositoryAndFactory<SalesAreaLicense> {

    public SalesAreaLicenseRepository() {
        super(SalesAreaLicenseRepository.class, SalesAreaLicense.class);
    }

    public SalesAreaLicense newSalesAreaLicense(
            final Occupancy occupancy,
            final String reference,
            final String name,
            final LocalDate startDate,
            final Party tenant,
            final Party landlord,
            final BigDecimal salesAreaNonFood,
            final BigDecimal salesAreaFood,
            final BigDecimal foodAndBeveragesArea
    ) {
        SalesAreaLicense license = new SalesAreaLicense();
        license.setOccupancy(occupancy);
        license.setType(agreementTypeRepository.find(SalesAreaLicenseTypeEnum.SALES_AREA_LICENSE));
        license.setReference(reference);
        license.setName(name);
        license.setStartDate(startDate);
        license.setSalesAreaNonFood(salesAreaNonFood);
        license.setSalesAreaFood(salesAreaFood);
        license.setFoodAndBeveragesArea(foodAndBeveragesArea);

        // app tenancy derived from the tenant
        license.setApplicationTenancyPath(tenant.getApplicationTenancy().getPath());

        repositoryService.persistAndFlush(license);

        final AgreementRoleType artLandlord = agreementRoleTypeRepository
                .find(LeaseAgreementRoleTypeEnum.LANDLORD);
        license.newRole(artLandlord, landlord, null, null);
        final AgreementRoleType artTenant = agreementRoleTypeRepository.find(
                LeaseAgreementRoleTypeEnum.TENANT);
        license.newRole(artTenant, tenant, null, null);
        return license;
    }

    public List<SalesAreaLicense> allSalesAreaLicenses() {
        return allInstances();
    }

    public SalesAreaLicense findByReference(final String reference){
        return (SalesAreaLicense) agreementRepository.findAgreementByTypeAndReference(agreementTypeRepository.find(
                SalesAreaLicenseTypeEnum.SALES_AREA_LICENSE), reference);
    }

    public SalesAreaLicense findMostRecentForOccupancy(final Occupancy occupancy) {
        return findByOccupancy(occupancy).stream().filter(x->x.getNext()==null).findFirst().orElse(null);
    }

    public List<SalesAreaLicense> findByOccupancy(final Occupancy occupancy) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        SalesAreaLicense.class,
                        "findByOccupancy",
                        "occupancy", occupancy));
    }

    @Inject
    protected AgreementTypeRepository agreementTypeRepository;

    @Inject
    protected AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    protected AgreementRepository agreementRepository;

    @Inject
    RepositoryService repositoryService;
}