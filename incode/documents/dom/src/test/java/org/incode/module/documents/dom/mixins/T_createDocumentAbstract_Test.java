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
package org.incode.module.documents.dom.mixins;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.documents.dom.impl.docs.DocumentAbstract;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;

public class T_createDocumentAbstract_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class SomeDomainObject {}

    @Mock
    private SomeDomainObject mockSomeDomainObject;

    @Mock
    private DocumentAbstract mockDocumentAbstract;

    T_createDocumentAbstract mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new T_createDocumentAbstract(mockSomeDomainObject) {
            @Override
            protected DocumentAbstract doCreate(
                    final DocumentTemplate template, final String additionalTextIfAny) {
                return mockDocumentAbstract;
            }
        };
    }

    public static class Hide_Test extends T_createDocumentAbstract_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }

    }

    public static class Choices_Test extends T_createDocumentAbstract_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }

    }

    public static class ActionInvocation_Test extends T_createDocumentAbstract_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }
    }


}