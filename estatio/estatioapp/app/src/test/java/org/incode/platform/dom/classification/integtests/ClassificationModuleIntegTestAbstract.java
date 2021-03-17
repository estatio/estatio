package org.incode.platform.dom.classification.integtests;

import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.ModuleAbstract;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.classification.dom.impl.classification.T_classifications;
import org.incode.module.classification.dom.impl.classification.T_classify;
import org.incode.module.classification.dom.impl.classification.T_unclassify;
import org.incode.platform.dom.classification.integtests.app.ClassificationAppModule;
import org.incode.platform.dom.classification.integtests.dom.classification.ClassificationModuleIntegrationSubmodule;

import org.estatio.module.base.integtests.BaseModuleIntegTestAbstract;

public abstract class ClassificationModuleIntegTestAbstract extends BaseModuleIntegTestAbstract {

    @XmlRootElement(name = "module")
    public static class MyModule extends ClassificationModuleIntegrationSubmodule {
        @Override
        public Set<org.apache.isis.applib.Module> getDependencies() {
            final Set<org.apache.isis.applib.Module> dependencies = super.getDependencies();
            dependencies.addAll(Sets.newHashSet(
                    new ClassificationAppModule(),
                    new FakeDataModule()
            ));
            return dependencies;
        }
    }

    public static ModuleAbstract module() {
        return new MyModule();
    }

    protected ClassificationModuleIntegTestAbstract() {
        super(module());
    }

    @Inject
    protected FakeDataService fakeData;

    protected T_classify mixinClassify(final Object classifiable) {
        return mixin(T_classify.class, classifiable);
    }
    protected T_unclassify mixinUnclassify(final Object classifiable) {
        return mixin(T_unclassify.class, classifiable);
    }
    protected T_classifications mixinClassifications(final Object classifiable) {
        return mixin(T_classifications.class, classifiable);
    }

}
