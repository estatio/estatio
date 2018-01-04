package org.incode.platform.dom.classification.integtests;

import javax.inject.Inject;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract3;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.classification.dom.impl.classification.T_classifications;
import org.incode.module.classification.dom.impl.classification.T_classify;
import org.incode.module.classification.dom.impl.classification.T_unclassify;
import org.incode.platform.dom.classification.integtests.app.ClassificationAppModule;
import org.incode.platform.dom.classification.integtests.dom.classification.ClassificationModuleIntegrationSubmodule;

public abstract class ClassificationModuleIntegTestAbstract extends IntegrationTestAbstract3 {

    public static ModuleAbstract module() {
        return new ClassificationModuleIntegrationSubmodule()
                .withAdditionalModules(
                        ClassificationAppModule.class,
                        FakeDataModule.class);
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
