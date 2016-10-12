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
package org.incode.module.documents.dom.impl.paperclips;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.documents.dom.impl.docs.DocumentAbstract;
import org.incode.module.documents.dom.impl.docs.DocumentAbstractForTesting;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.FixtureDatumFactoriesForJoda;

public class Paperclip_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(FixtureDatumFactoriesForJoda.dateTimes())
                    .withFixture(pojos(DocumentAbstract.class, DocumentAbstractForTesting.class))
                    .exercise(new PaperclipForTesting());
        }

    }

    public static class Title_Test extends Paperclip_Test {


        @Ignore
        @Test
        public void when_no_role() throws Exception {


        }

        @Ignore
        @Test
        public void when_has_a_role() throws Exception {


        }
    }


    public static class DocumentDate_Test extends Paperclip_Test {

        @Ignore
        @Test
        public void when_document() throws Exception {

        }

        @Ignore
        @Test
        public void when_document_template() throws Exception {

        }

    }


}