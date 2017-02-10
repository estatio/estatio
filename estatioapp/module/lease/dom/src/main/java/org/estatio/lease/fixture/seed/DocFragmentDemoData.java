/*
 *  Copyright 2014 Dan Haywood
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
package org.estatio.lease.fixture.seed;

import java.io.IOException;
import java.util.Objects;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.apache.commons.io.Charsets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.docfragment.dom.impl.DocFragment;
import org.incode.module.docfragment.dom.impl.DocFragmentRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DocFragmentDemoData {

    InvoiceDescription_DemoGbr(
            "Invoice", "description", "/GBR",
            "Invoice_description_ITA.docFragment.txt") {
        public FixtureScript script() {
            // subclasses are necessary because
            // FixtureScriptsSpecificationProvider's MultipleExecutionPolicy set to ONCE_PER_CLASS
            return new DocFragmentScript() {};
        }
    },
    InvoiceDescription_DemoNld(
            "Invoice", "description", "/NLD",
            "Invoice_description_ITA.docFragment.txt") {
        public FixtureScript script() {
            // subclasses are necessary because
            // FixtureScriptsSpecificationProvider's MultipleExecutionPolicy set to ONCE_PER_CLASS
            return new DocFragmentScript() {};
        }
    },
    InvoiceItemDescription_DemoGbr(
            "org.estatio.dom.lease.invoicing.InvoiceItemForLease", "description", "/GBR",
            "InvoiceItem_description_ITA.docFragment.txt") {
        public FixtureScript script() {
            return new DocFragmentScript() {};
        }
    },
    InvoiceItemDescription_DemoNld(
            "org.estatio.dom.lease.invoicing.InvoiceItemForLease", "description", "/NLD",
            "InvoiceItem_description_FRA.docFragment.txt") {
        public FixtureScript script() {
            return new DocFragmentScript() {};
        }
    },
    ;

    @Getter
    private final String objectType;
    @Getter
    private final String name;
    @Getter
    private final String atPath;
    private final String templateResourceName;

    public String getTemplateText() {
        return read(templateResourceName);
    }

    class DocFragmentScript extends FixtureScript {
        @Override
        public void execute(final ExecutionContext executionContext) {

            final DocFragment docFrag = docFragmentRepository
                    .findByObjectTypeAndNameAndApplicableToAtPath(objectType, name, atPath);
            if(docFrag != null && Objects.equals(docFrag.getAtPath(), atPath)) {
                return;
            }

            docFragmentRepository.create(objectType, name, atPath, getTemplateText());
        }

        @Inject
        DocFragmentRepository docFragmentRepository;
    }

    public abstract FixtureScript script();

    public static String read(final String resourceName) {
        try {
            return Resources.toString(Resources.getResource(DocFragmentDemoData.class, resourceName), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
