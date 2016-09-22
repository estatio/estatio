package org.estatio.dom.apptenancy;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioApplicationTenancyRepositoryForPartyProperty {


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
    EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepositoryForProperty;

    @Inject
    ApplicationTenancyRepository applicationTenancies;


}
