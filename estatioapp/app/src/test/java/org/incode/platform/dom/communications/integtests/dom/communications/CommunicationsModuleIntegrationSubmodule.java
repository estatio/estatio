package org.incode.platform.dom.communications.integtests.dom.communications;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.module.communications.CommunicationsModule;
import org.incode.platform.dom.communications.integtests.demo.CommunicationsModuleDemoDomSubmodule;
import org.incode.platform.dom.communications.integtests.dom.communications.dom.commchannels.CommunicationChannelOwnerLinkForDemoObjectWithNotes;
import org.incode.platform.dom.communications.integtests.dom.communications.dom.paperclips.PaperclipForDemoInvoice;

@XmlRootElement(name = "module")
public class CommunicationsModuleIntegrationSubmodule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new CommunicationsModule(),
                new CommunicationsModuleDemoDomSubmodule()
        );
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                deleteFrom(PaperclipForDemoInvoice.class);
                deleteFrom(CommunicationChannelOwnerLinkForDemoObjectWithNotes.class);
            }
        };
    }

    public static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> {}
    public static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> {}
    public static class ActionDomainEvent<S> extends
            org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> {}
}
