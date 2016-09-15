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
package org.incode.module.documents.dom.docs;

import org.junit.Before;
import org.junit.Test;

import org.incode.module.documents.dom.impl.docs.DocumentTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentTemplateTest {

    public static class FileSuffix {

        DocumentTemplate template;
        @Before
        public void setUp() throws Exception {
            template = new DocumentTemplate(null, null, null, "pdf", false, null, null, null, null, null, null);
        }

        @Test
        public void ends_with_suffix() throws Exception {
            final String docName = template.withFileSuffix("foo.pdf");

            assertThat(docName).isEqualTo("foo.pdf");
        }

        @Test
        public void ends_with_no_suffix() throws Exception {
            final String docName = template.withFileSuffix("foo");

            assertThat(docName).isEqualTo("foo.pdf");
        }

        @Test
        public void ends_with_other_suffix() throws Exception {
            final String docName = template.withFileSuffix("foo.doc");

            assertThat(docName).isEqualTo("foo.doc.pdf");
        }

        @Test
        public void ends_with_dot() throws Exception {
            final String docName = template.withFileSuffix("foo.");

            assertThat(docName).isEqualTo("foo..pdf");
        }
    }
}