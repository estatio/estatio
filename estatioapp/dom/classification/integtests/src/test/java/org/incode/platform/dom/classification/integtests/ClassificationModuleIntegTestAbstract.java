package org.incode.platform.dom.classification.integtests;

import javax.inject.Inject;

import org.junit.BeforeClass;

import org.apache.isis.core.integtestsupport.IntegrationTestAbstract2;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.classification.dom.impl.classification.T_classifications;
import org.incode.module.classification.dom.impl.classification.T_classify;
import org.incode.module.classification.dom.impl.classification.T_unclassify;
import org.incode.platform.dom.classification.integtests.app.ClassificationModuleAppManifest;
import org.incode.platform.dom.classification.integtests.demo.ExampleDomDemoDomSubmodule;

public abstract class ClassificationModuleIntegTestAbstract extends IntegrationTestAbstract2 {

    @BeforeClass
    public static void initClass() {
        bootstrapUsing(
                ClassificationModuleAppManifest.BUILDER
                    .withAdditionalModules(
                            ExampleDomDemoDomSubmodule.class,
                            ClassificationModuleIntegTestAbstract.class,
                            FakeDataModule.class
                    )
                    .build()
        ) ;
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
