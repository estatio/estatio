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

        public static class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<org.estatio.capex.dom.documents.HasBuyer._create> {
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
                final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION, optionality = Optionality.OPTIONAL) String reference,
                final boolean useNumereratorForReference,
                final String name,
                final Country country) {
            Organisation organisation = organisationRepository
                    .newOrganisation(reference, useNumereratorForReference, name, country);
            hasBuyer.setBuyer(organisation);
            return hasBuyer;
        }

        @Inject
        OrganisationRepository organisationRepository;

    }

}
