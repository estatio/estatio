package org.incode.platform.dom.classification.integtests.tests.category.taxonomy;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.services.wrapper.InvalidException;

import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_tearDown;

import static org.assertj.core.api.Assertions.assertThat;

public class Taxonomy_applicable_IntegTest extends ClassificationModuleIntegTestAbstract {

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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_tearDown(), null);
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3(), null);
    }

    @Test
    public void can_add_applicability_for_different_domain_types_with_same_atPath() {
        // given
        Taxonomy frenchColours = (Taxonomy) categoryRepository.findByParentAndName(null, "French Colours");
        assertThat(applicabilityRepository.findByDomainTypeAndUnderAtPath(OtherObjectWithAtPath.class, "/FRA")).isEmpty();

        // when
        wrap(frenchColours).applicable("/FRA", OtherObjectWithAtPath.class.getName());

        // then
        assertThat(applicabilityRepository.findByDomainTypeAndUnderAtPath(OtherObjectWithAtPath.class, "/FRA")).hasSize(1);
    }

    @Test
    public void can_add_applicability_for_same_domain_types_with_different_atPath() {

        // eg given an applicability for "/ITA" and 'DemoObject', can also add an applicability for "/ITA/MIL" and 'DemoObject'
        Taxonomy italianColours = (Taxonomy) categoryRepository.findByParentAndName(null, "Italian Colours");
        final List<Applicability> byDomainTypeAndUnderAtPath = applicabilityRepository.findByDomainTypeAndUnderAtPath(DemoObjectWithAtPath.class, "/ITA");
        assertThat(byDomainTypeAndUnderAtPath).hasSize(2);
        assertThat(byDomainTypeAndUnderAtPath).extracting(Applicability::getTaxonomy).extracting(Taxonomy::getFullyQualifiedName).containsOnly("Italian Colours", "Sizes");

        // when
        wrap(italianColours).applicable("/ITA/MIL", DemoObjectWithAtPath.class.getName());

        // then
        final List<Applicability> byDomainTypeAndUnderAtPathNew = applicabilityRepository.findByDomainTypeAndUnderAtPath(DemoObjectWithAtPath.class, "/ITA/MIL");
        assertThat(byDomainTypeAndUnderAtPathNew).hasSize(3);
        assertThat(byDomainTypeAndUnderAtPathNew).extracting(Applicability::getTaxonomy).extracting(Taxonomy::getFullyQualifiedName).containsOnly("Italian Colours", "Sizes");
    }

    @Test
    public void cannot_add_applicability_if_already_has_applicability_for_given_domainType_and_atPath() {

        // eg set up for "/ITA" and 'DemoObject', cannot add again
        // given
        Taxonomy italianColours = (Taxonomy) categoryRepository.findByParentAndName(null, "Italian Colours");

        // then
        expectedException.expect(InvalidException.class);
        expectedException.expectMessage("Already applicable for '/ITA' and '" + DemoObjectWithAtPath.class.getName() + "'");

        // when
        wrap(italianColours).applicable("/ITA", DemoObjectWithAtPath.class.getName());

    }

}