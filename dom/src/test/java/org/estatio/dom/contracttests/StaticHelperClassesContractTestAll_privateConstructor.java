package org.estatio.dom.contracttests;

import org.junit.Test;

import org.estatio.dom.ApplicationSettingCreator;
import org.estatio.dom.WithCodeGetter;
import org.estatio.dom.WithDescriptionGetter;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.WithTitleGetter;
import org.estatio.dom.ApplicationSettingCreator.Helper;
import org.estatio.dom.WithNameGetter.ToString;
import org.estatio.dom.utils.PrivateConstructorTester;

public class StaticHelperClassesContractTestAll_privateConstructor {

    @Test
    public void cover() throws Exception {
        exercise(ApplicationSettingCreator.Helper.class);
        exercise(WithCodeGetter.ToString.class);
        exercise(WithDescriptionGetter.ToString.class);
        exercise(WithNameGetter.ToString.class);
        exercise(WithReferenceGetter.ToString.class);
        exercise(WithTitleGetter.ToString.class);
    }

    private static void exercise(final Class<?> cls) throws Exception {
        new PrivateConstructorTester(cls).exercise();
    }
}
