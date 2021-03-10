package org.incode.platform.dom.classification.integtests.tests.category;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryRepository_findByParentAndReference_IntegTest extends ClassificationModuleIntegTestAbstract {

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

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3(), null);
    }

    @Test
    public void happy_case() {
        // given
        Category parentLarge = categoryRepository.findByReference("LGE");

        // when
        Category childLarge = categoryRepository.findByParentAndReference(parentLarge, "XL");

        // then
        assertThat(childLarge.getName()).isEqualTo("Larger");
        assertThat(childLarge.getParent()).isEqualTo(parentLarge);
    }

    @Test
    public void when_none() {
        // given
        Category parentLarge = categoryRepository.findByReference("LGE");

        // when
        Category childLarge = categoryRepository.findByParentAndReference(parentLarge, "XXXXL");

        // then
        assertThat(childLarge).isNull();
    }

}