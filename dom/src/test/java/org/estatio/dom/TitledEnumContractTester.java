package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.isis.core.commons.lang.StringUtils;

public class TitledEnumContractTester<T extends TitledEnum> {

    private Enum<?>[] enumValues;
    private Class<? extends Enum<?>> cls;

    /**
     * @param enumType
     */
    public TitledEnumContractTester(Class<? extends Enum<?>> enumType) {
        this.cls = enumType;
        this.enumValues = enumType.getEnumConstants();
    }

    public void test() {
        System.out.println("TitledEnumContractTester: " + cls.getName());

        for (Enum<?> enumValue: enumValues) {
            final TitledEnum titled = (TitledEnum) enumValue;
            final String enumName = enumValue.name();
            assertThat(enumValue.getClass().getName()+"#"+enumName, titled.title(), is(StringUtils.enumTitle(enumName)));
        }
    }


}
