/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.isis.core.commons.lang.StringExtensions;

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
            assertThat(enumValue.getClass().getName()+"#"+enumName, titled.title(), is(StringExtensions.enumTitle(enumName)));
        }
    }


}
