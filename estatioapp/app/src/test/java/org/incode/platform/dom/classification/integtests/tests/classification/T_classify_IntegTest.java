package org.incode.platform.dom.classification.integtests.tests.classification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
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

public class T_classify_IntegTest extends ClassificationModuleIntegTestAbstract {

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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3(), null);
    }

    @Test
    public void when_applicability_and_no_classification() {
        // given
        DemoObjectWithAtPath demoBip = demoObjectMenu.listAllDemoObjectsWithAtPath()
                .stream()
                .filter(demoObject -> demoObject.getName().equals("Demo bip (in Milan)"))
                .findFirst()
                .get();
        assertThat(classificationRepository.findByClassified(demoBip)).isEmpty();

        // when
        final ClassificationForDemoObjectWithAtPath.classify classification = factoryService.mixin(ClassificationForDemoObjectWithAtPath.classify.class, demoBip);
        Collection<Taxonomy> choices0Classify = classification.choices0Classify();
        assertThat(choices0Classify)
                .extracting(Taxonomy::getName)
                .containsOnly("Italian Colours", "Sizes");

        List<String> categoryNames = new ArrayList<>();

        for (Taxonomy taxonomy : choices0Classify) {
            Category category = classification.default1Classify(taxonomy);
            categoryNames.add(category.getName());
            wrap(classification).classify(taxonomy, category);
        }

        // then
        assertThat(classificationRepository.findByClassified(demoBip))
                .extracting(Classification::getCategory)
                .extracting(Category::getName)
                .containsOnlyElementsOf(categoryNames);
    }

    @Test
    public void cannot_classify_when_applicability_but_classifications_already_defined() {
        // given
        DemoObjectWithAtPath demoFooInItaly = demoObjectMenu.listAllDemoObjectsWithAtPath()
                .stream()
                .filter(demoObject -> demoObject.getName().equals("Demo foo (in Italy)"))
                .findFirst()
                .get();
        assertThat(classificationRepository.findByClassified(demoFooInItaly))
                .extracting(Classification::getCategory)
                .extracting(Category::getName)
                .contains("Red", "Medium");

        final ClassificationForDemoObjectWithAtPath.classify classification = factoryService.mixin(ClassificationForDemoObjectWithAtPath.classify.class, demoFooInItaly);

        // when
        final String message = classification.disableClassify().toString();

        // then
        assertThat(message).isEqualTo("tr: There are no classifications that can be added");
    }

    @Test
    public void cannot_classify_when_no_applicability_for_domain_type() {
        // given
        OtherObjectWithAtPath otherBaz = otherObjectMenu.listAllOtherObjectsWithAtPath()
                .stream()
                .filter(otherObject -> otherObject.getName().equals("Other baz (Global)"))
                .findFirst()
                .get();
        assertThat(applicabilityRepository.findByDomainTypeAndUnderAtPath(otherBaz.getClass(), otherBaz.getAtPath())).isEmpty();

        final ClassificationForOtherObjectWithAtPath.classify classification = factoryService.mixin(ClassificationForOtherObjectWithAtPath.classify.class, otherBaz);

        // when
        final String message = classification.disableClassify().toString();

        // then
        assertThat(message).isEqualTo("tr: There are no classifications that can be added");
    }

    @Test
    public void cannot_classify_when_no_applicability_for_atPath() {
        // given
        OtherObjectWithAtPath otherBarInFrance = otherObjectMenu.listAllOtherObjectsWithAtPath()
                .stream()
                .filter(otherObject -> otherObject.getName().equals("Other bar (in France)"))
                .findFirst()
                .get();
        assertThat(applicabilityRepository.findByDomainTypeAndUnderAtPath(otherBarInFrance.getClass(), otherBarInFrance.getAtPath())).isEmpty();

        final ClassificationForOtherObjectWithAtPath.classify classification = factoryService.mixin(ClassificationForOtherObjectWithAtPath.classify.class, otherBarInFrance);

        // when
        final String message = classification.disableClassify().toString();

        // then
        assertThat(message).isEqualTo("tr: There are no classifications that can be added");
    }

}