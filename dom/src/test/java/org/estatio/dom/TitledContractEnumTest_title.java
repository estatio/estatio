package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.isis.core.commons.lang.StringUtils;

public class TitledContractEnumTest_title<T extends TitledEnum> {

    private Enum<?>[] enumValues;

    /**
     * For {@link TitledContractEnumAutoTest_compareTo auto-testing}.
     */
    public TitledContractEnumTest_title<T> with(Enum<?>[] enumValues) {
        this.enumValues = enumValues;
        return this;
    }

    public void checkAllTitles() {
        for (Enum<?> enumValue: enumValues) {
            final TitledEnum titled = (TitledEnum) enumValue;
            final String enumName = enumValue.name();
            assertThat(enumValue.getClass().getName()+"#"+enumName, titled.title(), is(StringUtils.enumTitle(enumName)));
        }
    }


}
