package org.estatio.capex.dom.documents;

import javax.inject.Inject;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.OrganisationRepository;

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
                final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION, optionality = Optionality.OPTIONAL) String reference,
                final boolean useNumereratorForReference,
                final String name,
                final Country country) {
            Organisation organisation = organisationRepository
                    .newOrganisation(reference, useNumereratorForReference, name, country);
            hasSeller.setSeller(organisation);
            return hasSeller;
        }

        @Inject
        OrganisationRepository organisationRepository;

    }

}
