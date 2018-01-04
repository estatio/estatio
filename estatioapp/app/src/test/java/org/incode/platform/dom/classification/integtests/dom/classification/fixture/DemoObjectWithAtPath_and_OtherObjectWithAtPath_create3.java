package org.incode.platform.dom.classification.integtests.dom.classification.fixture;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.dom.classification.dom.classification.demowithatpath.ClassificationForDemoObjectWithAtPath;

import lombok.Getter;

public class DemoObjectWithAtPath_and_OtherObjectWithAtPath_create3
        extends FixtureScript {

    @Getter
    private List<DemoObjectWithAtPath> demoObjects = Lists.newArrayList();

    @Getter
    private List<OtherObjectWithAtPath> otherObjects = Lists.newArrayList();

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // italian taxonomy applicable only to Italian DemoObject and OtherObject
        Taxonomy italianColours = categoryRepository.createTaxonomy("Italian Colours");
        italianColours.setReference("ITACOL");
        Category italianRed = italianColours.addChild("Red", "RED", null);
        Category italianGreen = italianColours.addChild("Green", "GREEN", null);
        Category italianWhite = italianColours.addChild("White", "WHITE", null);

        wrap(italianColours).applicable("/ITA", DemoObjectWithAtPath.class.getName());
        wrap(italianColours).applicable("/ITA", OtherObjectWithAtPath.class.getName());

        // french taxonomy applicable only to French DemoObject (and not to OtherObject even if with FRA app tenancy)
        Taxonomy frenchColours = categoryRepository.createTaxonomy("French Colours");
        frenchColours.setReference("FRCOL");
        Category frenchRed = frenchColours.addChild("Red", "FRRED", null);
        Category frenchWhite = frenchColours.addChild("White", "FRWHITE", null);
        Category frenchBlue = frenchColours.addChild("Blue", "FRBLUE", null);

        wrap(frenchColours).applicable("/FRA", DemoObjectWithAtPath.class.getName());

        // global taxonomy applicable only to DemoObject (any app tenancy)
        Taxonomy globalSizes = categoryRepository.createTaxonomy("Sizes");
        globalSizes.setReference("SIZES");
        Category large = globalSizes.addChild("Large", "LGE", 1);
        Category medium = globalSizes.addChild("Medium", "M", 2);
        Category small = globalSizes.addChild("Small", "SML", 3);

        Category largeLargest = large.addChild("Largest", "XXL", 1);
        Category largeLarger = large.addChild("Larger", "XL", 2);
        Category largeLarge = large.addChild("Large", "L", 3);

        Category smallSmall = small.addChild("Small", "S", 1);
        Category smallSmaller = small.addChild("Smaller", "XS", 2);
        Category smallSmallest = small.addChild("Smallest", "XXS", 3);

        wrap(globalSizes).applicable("/", DemoObjectWithAtPath.class.getName());

        // create a sample set of DemoObject and OtherObject, for various app tenancies

        final DemoObjectWithAtPath demoFooInItaly = createDemo("Demo foo (in Italy)", "/ITA", executionContext);
        final DemoObjectWithAtPath demoBarInFrance = createDemo("Demo bar (in France)", "/FRA", executionContext);
        final DemoObjectWithAtPath demoBaz = createDemo("Demo baz (Global)", "/", executionContext);
        final DemoObjectWithAtPath demoBip = createDemo("Demo bip (in Milan)", "/ITA/I-MIL", executionContext);
        final DemoObjectWithAtPath demoBop = createDemo("Demo bop (in Paris)", "/FRA/F-PAR", executionContext);

        final OtherObjectWithAtPath otherFooInItaly = createOther("Other foo (in Italy)", "/ITA", executionContext);

        final OtherObjectWithAtPath otherBarInFrance = createOther("Other bar (in France)", "/FRA", executionContext);
        final OtherObjectWithAtPath otherBaz = createOther("Other baz (Global)", "/", executionContext);
        final OtherObjectWithAtPath otherBip = createOther("Other bip (in Milan)", "/ITA/I-MIL", executionContext);
        final OtherObjectWithAtPath otherBop = createOther("Other bop (in Paris)", "/FRA/F-PAR", executionContext);

        // classify DemoObject

        wrap(mixin(ClassificationForDemoObjectWithAtPath.classify.class, demoFooInItaly)).classify(italianColours, italianRed);

        wrap(mixin(ClassificationForDemoObjectWithAtPath.classify.class, demoFooInItaly)).classify(globalSizes, medium);

        wrap(mixin(ClassificationForDemoObjectWithAtPath.classify.class, demoBarInFrance)).classify(globalSizes, smallSmaller);

        // leave OtherObject unclassified

    }

    private DemoObjectWithAtPath createDemo(
            final String name,
            final String atPath,
            final ExecutionContext executionContext) {
        final DemoObjectWithAtPath demoObject = wrap(demoObjectMenu).createDemoObjectWithAtPath(name, atPath);
        demoObjects.add(demoObject);
        return executionContext.addResult(this, demoObject);
    }

    private OtherObjectWithAtPath createOther(
            final String name,
            final String atPath,
            final ExecutionContext executionContext) {
        final OtherObjectWithAtPath otherObject = wrap(otherObjectMenu).createOtherObjectWithAtPath(name, atPath);
        otherObjects.add(otherObject);
        return executionContext.addResult(this, otherObject);
    }

    //region > injected services
    @Inject
    DemoObjectWithAtPathMenu demoObjectMenu;
    @Inject
    OtherObjectWithAtPathMenu otherObjectMenu;
    @Inject
    CategoryRepository categoryRepository;
    //endregion

    @Inject
    FactoryService factoryService;

}
