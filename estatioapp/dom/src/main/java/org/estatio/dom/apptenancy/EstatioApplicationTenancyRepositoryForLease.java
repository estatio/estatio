package org.estatio.dom.apptenancy;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioApplicationTenancyRepositoryForLease {


    public ApplicationTenancy findOrCreateTenancyFor(final Lease lease) {
        return estatioApplicationTenancyRepositoryForProperty.findOrCreateTenancyFor(lease.getProperty());
    }

    public ApplicationTenancy findOrCreateTenancyFor(final LeaseItem leaseItem) {
        return estatioApplicationTenancyRepositoryForPartyProperty.findOrCreateTenancyFor(leaseItem.getLease().getProperty(), leaseItem.getLease().getPrimaryParty());
    }


    @Inject
    EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepositoryForProperty;

    @Inject
    EstatioApplicationTenancyRepositoryForPartyProperty estatioApplicationTenancyRepositoryForPartyProperty;


}
