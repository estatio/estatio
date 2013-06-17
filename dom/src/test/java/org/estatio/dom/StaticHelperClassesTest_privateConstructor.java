package org.estatio.dom;

import org.junit.Test;

public class StaticHelperClassesTest_privateConstructor {

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
