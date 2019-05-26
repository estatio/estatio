package org.estatio.module.capex.dom.documents;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;

import lombok.Getter;

public interface HasBuyer {

    Organisation getBuyer();
    void setBuyer(Organisation organisation);

    @Mixin(method = "act")
    public class _lookup {

        public static class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<_lookup> {
        }

        @Getter
        private final HasBuyer hasBuyer;

        @Inject
        public ServiceRegistry2 serviceRegistry2;

        public _lookup(final HasBuyer hasBuyer) {
            this.hasBuyer = hasBuyer;
        }

        @Action(
                semantics = SemanticsOf.IDEMPOTENT,
                domainEvent = _lookup.ActionDomainEvent.class
        )
        @ActionLayout(
                position = ActionLayout.Position.RIGHT
        )
        @MemberOrder(name = "buyer", sequence = "1")
        public HasBuyer act(
                final Organisation buyer
        ) {
            hasBuyer.setBuyer(buyer);
            return hasBuyer;
        }

    }

    @Mixin(method = "act")
    public class _create {

        public static class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<HasBuyer._create> {
        }

        @Getter
        private final HasBuyer hasBuyer;

        @Inject
        public ServiceRegistry2 serviceRegistry2;

        public _create(final HasBuyer hasBuyer) {
            this.hasBuyer = hasBuyer;
        }

        @Action(
                semantics = SemanticsOf.IDEMPOTENT,
                domainEvent = _lookup.ActionDomainEvent.class
        )
        @ActionLayout(
                position = ActionLayout.Position.RIGHT
        )
        @MemberOrder(name = "buyer", sequence = "2")
        public HasBuyer act(
                final String name,
                final String chamberOfCommerceCode,
                final Country country) {
            Organisation organisation = organisationRepository
                    .newOrganisation(null, true, name, chamberOfCommerceCode, country);
            hasBuyer.setBuyer(organisation);
            return hasBuyer;
        }

        public String validateAct(
                final String name,
                final String chamberOfCommerceCode,
                final Country country) {
            final String countryAtPath = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country).getPath();

            return chamberOfCommerceCode == null && Stream.of("/FRA", "/BEL").anyMatch(countryAtPath::startsWith) ?
                    "Chamber of Commerce code is mandatory for French and Belgian organisations" :
                    null;
        }

        @Inject
        OrganisationRepository organisationRepository;

        @Inject
        EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    }

}
