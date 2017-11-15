package org.estatio.module.party;

import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.base.platform.applib.Module;
import org.estatio.module.base.platform.applib.ModuleAbstract;
import org.estatio.module.numerator.EstatioNumeratorModule;
import org.estatio.module.party.dom.CommunicationChannelOwnerLinkForParty;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationPreviousName;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRegistration;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.paperclips.PaperclipForParty;
import org.estatio.module.party.dom.relationship.PartyRelationship;
import org.estatio.module.party.dom.role.PartyRole;

public final class EstatioPartyModule extends ModuleAbstract {

    public EstatioPartyModule(){}

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new EstatioNumeratorModule());
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
                deleteFrom(Organisation.class);
                deleteFrom(Person.class);
                deleteFrom(Party.class);
            }
        };
    }


    public abstract static class ActionDomainEvent<S>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }

    public abstract static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }

    public abstract static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }

}
