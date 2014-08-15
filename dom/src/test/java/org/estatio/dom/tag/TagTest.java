/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom.tag;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.WithNameComparable;

public class TagTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(Taggable.class, new Taggable() {
                    }, new Taggable() {
                    })
                    .exercise(new Tag());
        }

    }

    public static class CompareTo extends ComparableContractTest_compareTo<Tag> {

        public static class SomeTaggableObject extends EstatioDomainObject<SomeTaggableObject>
                implements Taggable, WithNameComparable<SomeTaggableObject> {

            public SomeTaggableObject() {
                super("name");
            }

            public SomeTaggableObject(String name) {
                this();
                setName(name);
            }

            private String name;
            @Override
            public String getName() {
                return name;
            }
            @Override
            public void setName(String name) {
                this.name = name;
            }
        }

        private Taggable taggable1;
        private Taggable taggable2;

        @Before
        public void setUp() throws Exception {
            taggable1 = new SomeTaggableObject("A");
            taggable2 = new SomeTaggableObject("B");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<Tag>> orderedTuples() {
            return listOf(
                    listOf(
                            newTag(null, null),
                            newTag(taggable1, null),
                            newTag(taggable1, null),
                            newTag(taggable2, null)
                    ),
                    listOf(
                            newTag(taggable1, null),
                            newTag(taggable1, "Abc"),
                            newTag(taggable1, "Abc"),
                            newTag(taggable1, "Def")
                    )
            );
        }

        private Tag newTag(
                final Taggable taggable,
                final String name) {
            final Tag tag = new Tag();

            tag.setTaggable(taggable);
            tag.setName(name);
            return tag;
        }

    }

}