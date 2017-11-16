package org.estatio.module.party;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.base.platform.applib.Module;
import org.isisaddons.module.base.platform.applib.ModuleAbstract;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.communications.IncodeDomCommunicationsModule;
import org.estatio.module.countryapptenancy.EstatioCountryAppTenancyModule;
import org.estatio.module.numerator.EstatioNumeratorModule;
import org.estatio.module.party.dom.CommunicationChannelOwnerLinkForParty;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationPreviousName;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRegistration;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.paperclips.PaperclipForParty;
import org.estatio.module.party.dom.relationship.PartyRelationship;
import org.estatio.module.party.dom.role.PartyRole;

@XmlRootElement(name = "module")
public final class EstatioPartyModule extends ModuleAbstract {

    public EstatioPartyModule(){}

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new EstatioNumeratorModule(),
                new EstatioCountryAppTenancyModule(),
                new IncodeDomCommunicationsModule()   // for communication channels
        );
    }


    @Override
    public FixtureScript getRefDataSetupFixture() {
        return new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
            }
        };
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {

                deleteFrom(CommunicationChannelOwnerLinkForParty.class);
                deleteFrom(PaperclipForParty.class);
                deleteFrom(OrganisationPreviousName.class);
                deleteFrom(PartyRegistration.class);
                deleteFrom(PartyRelationship.class);
                deleteFrom(PartyRole.class);

                final List<Organisation> before = organisationRepository.allOrganisations();
                deleteFrom(Organisation.class);
                final List<Organisation> after = organisationRepository.allOrganisations();
                deleteFrom(Person.class);
                deleteFrom(Party.class);
            }
            @Inject
            OrganisationRepository organisationRepository;
        };

    }


    public abstract static class ActionDomainEvent<S>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }

    public abstract static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }

    public abstract static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }

}
