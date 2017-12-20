package org.incode.platform.dom.classification.integtests.tests.classification;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.Classification;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.dom.classification.dom.classification.demowithatpath.ClassificationForDemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.dom.classification.dom.classification.otherwithatpath.ClassificationForOtherObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;

import static org.assertj.core.api.Assertions.assertThat;

public class T_unclassify_IntegTest extends ClassificationModuleIntegTestAbstract {

    @Inject
    ClassificationRepository classificationRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    ApplicabilityRepository applicabilityRepository;

    @Inject
    DemoObjectWithAtPathMenu demoObjectMenu;
    @Inject
    OtherObjectWithAtPathMenu otherObjectMenu;

    @Inject
    ApplicationTenancyService applicationTenancyService;
    @Inject
    FactoryService factoryService;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3(), null);
    }

    @Test
    public void enabled_when_classifications_exist() {
        // given
        DemoObjectWithAtPath demoFooInItaly = demoObjectMenu.listAllDemoObjectsWithAtPath()
                .stream()
                .filter(demoObject -> demoObject.getName().equals("Demo foo (in Italy)"))
                .findFirst()
                .get();
        final List<Classification> byClassified = classificationRepository.findByClassified(demoFooInItaly);
        assertThat(byClassified).hasSize(2);

        // when
        final ClassificationForDemoObjectWithAtPath.unclassify unclassify = factoryService.mixin(ClassificationForDemoObjectWithAtPath.unclassify.class, demoFooInItaly);
        wrap(unclassify).unclassify(byClassified.get(0));

        // then
        assertThat(classificationRepository.findByClassified(demoFooInItaly)).hasSize(1);
    }

    @Test
    public void disabled_when_no_classification_to_remove() {

        // given "Other bar (in Paris)", that has no classifications
        // given
        OtherObjectWithAtPath otherBarInFrance = otherObjectMenu.listAllOtherObjectsWithAtPath()
                .stream()
                .filter(otherObject -> otherObject.getName().equals("Other bar (in France)"))
                .findFirst()
                .get();
        assertThat(classificationRepository.findByClassified(otherBarInFrance)).isEmpty();

        final ClassificationForOtherObjectWithAtPath.unclassify unclassify = factoryService.mixin(ClassificationForOtherObjectWithAtPath.unclassify.class, otherBarInFrance);

        // when
        final String message = unclassify.disableUnclassify().toString();

        // then
        assertThat(message).isEqualTo("tr: No classifications to delete");
    }

}