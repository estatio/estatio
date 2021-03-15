package org.estatio.module.capex.dom.documents;

import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;

import lombok.Getter;

public interface HasSeller {

    Organisation getSeller();

    void setSeller(Organisation organisation);

    @Mixin(method = "act")
    public class _lookup {

        public static class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<_lookup> {
        }

        @Getter
        private final HasSeller hasSeller;

        @Inject
        public ServiceRegistry2 serviceRegistry2;

        public _lookup(final HasSeller hasSeller) {
            this.hasSeller = hasSeller;
        }

        @Action(
                semantics = SemanticsOf.IDEMPOTENT,
                domainEvent = _lookup.ActionDomainEvent.class
        )
        @MemberOrder(name = "seller", sequence = "1")
        public HasSeller act(
                final Organisation seller
        ) {
            hasSeller.setSeller(seller);
            return hasSeller;
        }

    }

    @Mixin(method = "act")
    public class _create {

        public static class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<HasSeller._create> {
        }

        @Getter
        private final HasSeller hasSeller;

        @Inject
        public ServiceRegistry2 serviceRegistry2;

        public _create(final HasSeller hasSeller) {
            this.hasSeller = hasSeller;
        }

        @Action(
                semantics = SemanticsOf.IDEMPOTENT,
                domainEvent = _lookup.ActionDomainEvent.class
        )
        @MemberOrder(name = "seller", sequence = "2")
        public HasSeller act(
                final String name,
                final String chamberOfCommerceCode,
                final Country country) {
            Organisation organisation = organisationRepository
                    .newOrganisation(null, true, name, chamberOfCommerceCode, country);
            hasSeller.setSeller(organisation);
            return hasSeller;
        }

        public String validateAct(
                final String name,
                final String chamberOfCommerceCode,
                final Country country) {
            final String countryAtPath = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country).getPath();

            if (chamberOfCommerceCode == null && Stream.of("/FRA", "/BEL").anyMatch(countryAtPath::startsWith))
                return "Chamber of Commerce code is mandatory for French and Belgian organisations";

            Optional<Organisation> orgIfAny = organisationRepository.findByChamberOfCommerceCode(chamberOfCommerceCode)
                    .stream()
                    .filter(org -> org.getApplicationTenancy().getPath().equals(countryAtPath))
                    .findFirst();

            return orgIfAny.map(organisation -> String.format("An organisation for this country and chamber of commerce code already exists: %s [%s]", organisation.getName(), organisation.getReference())).orElse(null);
        }

        @Inject
        OrganisationRepository organisationRepository;

        @Inject
        EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    }

}
