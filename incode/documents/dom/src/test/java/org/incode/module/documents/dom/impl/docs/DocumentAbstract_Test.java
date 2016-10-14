/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.impl.docs;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.dom.impl.types.DocumentTypeForTesting;

import org.incode.module.base.dom.AbstractBeanPropertiesTest;
import org.incode.module.base.dom.FixtureDatumFactoriesForApplib;

public class DocumentAbstract_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(FixtureDatumFactoriesForApplib.blobs())
                    .withFixture(FixtureDatumFactoriesForApplib.clobs())
                    .withFixture(pojos(DocumentType.class, DocumentTypeForTesting.class))
                    .exercise(new DocumentAbstractForTesting());
        }

    }


    public static class Constructor_Test extends DocumentAbstract_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }
    }

    public static class Blob_Test extends DocumentAbstract_Test {

        @Ignore
        @Test
        public void set_happy_case() throws Exception {

        }

        @Ignore
        @Test
        public void hidden_if_sort_is_not_a_blob() throws Exception {

        }

    }

    public static class Clob_Test extends DocumentAbstract_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }


        @Ignore
        @Test
        public void hidden_if_sort_is_not_a_clob() throws Exception {

        }
    }

    public static class TextData_Test extends DocumentAbstract_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }


        @Ignore
        @Test
        public void hidden_if_sort_is_not_a_text() throws Exception {

        }
    }


    public static class AsDataSource_Test extends Document_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }


}