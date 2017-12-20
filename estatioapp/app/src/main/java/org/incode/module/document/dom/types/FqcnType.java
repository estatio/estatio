package org.incode.module.document.dom.types;

import javax.inject.Inject;

import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.spec.AbstractSpecification2;

import org.incode.module.document.dom.services.ClassService;

public class FqcnType {

    private FqcnType() {}

    public static class Meta {

        public final static int MAX_LEN = 254;

        private Meta() {}

        public static class Specification extends AbstractSpecification2<String> {

            @Override
            public TranslatableString satisfiesTranslatableSafely(final String fullyQualifiedClassName) {
                return classService.validateClassHasAccessibleNoArgConstructor(fullyQualifiedClassName);
            }

            @Inject
            ClassService classService;
        }

    }

}
