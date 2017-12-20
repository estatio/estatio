package org.incode.platform.dom.classification.integtests.tests.classification;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.Classification;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;

import static org.assertj.core.api.Assertions.assertThat;

public class Classification_category_IntegTest extends ClassificationModuleIntegTestAbstract {

    @Inject
    ClassificationRepository classificationRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    ApplicabilityRepository applicabilityRepository;

    @Inject
    DemoObjectWithAtPathMenu demoObjectMenu;
    @Inject
    ApplicationTenancyService applicationTenancyService;

    Object classified;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3(), null);
    }

    @Ignore
    public void happy_case() {
        // given
        DemoObjectWithAtPath demoFooInItaly = demoObjectMenu.listAllDemoObjectsWithAtPath().stream()
                .filter(demoObject -> demoObject.getName().equals("Demo foo (in Italy)"))
                .findFirst()
                .get();

        Classification italianClassificationRed = classificationRepository.findByClassified(demoFooInItaly)
                .stream()
                .filter(classification -> classification.getCategory().getName().equals("Red"))
                .findFirst()
                .get();

        Category italianGreen = categoryRepository.findByReference("GREEN");

        // when
        wrap(italianClassificationRed).setCategory(italianGreen);

        // then
        assertThat(classificationRepository.findByClassified(demoFooInItaly))
                .extracting(Classification::getCategory)
                .extracting(Category::getName)
                .contains("Green")
                .doesNotContain("Red");
    }

}