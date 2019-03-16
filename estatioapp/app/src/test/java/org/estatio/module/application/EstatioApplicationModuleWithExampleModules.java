package org.estatio.module.application;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.incode.module.docrendering.gotenberg.fixture.fake.GotenbergRenderingFakeModule;
import org.incode.platform.dom.alias.integtests.AliasModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.communications.integtests.CommunicationsModuleIntegTestAbstract;
import org.incode.platform.dom.communications.integtests.app.services.FakeCommsServiceModule;
import org.incode.platform.dom.docfragment.integtests.DocFragmentModuleIntegTestAbstract;
import org.incode.platform.dom.document.integtests.DocumentModuleIntegTestAbstract;

import org.estatio.module.numerator.integtests.NumeratorModuleIntegTestAbstract;

@XmlRootElement(name = "module")
public class EstatioApplicationModuleWithExampleModules extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new EstatioApplicationModule(),

                // supporting example doms for these generic subdomains
                NumeratorModuleIntegTestAbstract.module(),
                AliasModuleIntegTestAbstract.module(), // alias actually isn't used by estatio
                ClassificationModuleIntegTestAbstract.module(),
                CommunicationsModuleIntegTestAbstract.module(),
                DocFragmentModuleIntegTestAbstract.module(),
                DocumentModuleIntegTestAbstract.module(),

                new FakeCommsServiceModule(),
                new GotenbergRenderingFakeModule()
        );
    }
}
