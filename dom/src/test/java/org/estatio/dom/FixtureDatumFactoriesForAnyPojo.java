package org.estatio.dom;

import org.estatio.dom.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForAnyPojo {

    @SuppressWarnings("unchecked")
    public static <T> FixtureDatumFactory<T> pojos(Class<T> compileTimeType, Class<? extends T> runtimeType) {
        try {
            final T obj1 = runtimeType.newInstance();
            final T obj2 = runtimeType.newInstance();
            return new FixtureDatumFactory<T>(compileTimeType, obj1, obj2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
