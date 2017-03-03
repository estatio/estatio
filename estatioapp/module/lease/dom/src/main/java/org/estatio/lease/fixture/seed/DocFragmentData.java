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
public enum DocFragmentData {

    InvoicePreliminaryLetterDescriptionIta(
            "org.estatio.dom.invoice.Invoice", "preliminaryLetterDescription", "/ITA",
            "Invoice_preliminaryLetterDescription_ITA.docFragment.ftl") {
        public FixtureScript script() {
            // subclasses are necessary because
            // FixtureScriptsSpecificationProvider's MultipleExecutionPolicy set to ONCE_PER_CLASS
            return new DocFragmentScript() {};
        }
    },
    InvoiceDescriptionIta(
            "org.estatio.dom.invoice.Invoice", "description", "/ITA",
            "Invoice_description_ITA.docFragment.ftl") {
        public FixtureScript script() {
            // subclasses are necessary because
            // FixtureScriptsSpecificationProvider's MultipleExecutionPolicy set to ONCE_PER_CLASS
            return new DocFragmentScript() {};
        }
    },
    InvoiceItemDescriptionIta(
            "org.estatio.dom.lease.invoicing.InvoiceItemForLease", "description", "/ITA",
            "InvoiceItem_description_ITA.docFragment.ftl") {
        public FixtureScript script() {
            // subclasses are necessary because
            // FixtureScriptsSpecificationProvider's MultipleExecutionPolicy set to ONCE_PER_CLASS
            return new DocFragmentScript() {};
        }
    },

    InvoicePreliminaryLetterDescriptionFra(
            "org.estatio.dom.invoice.Invoice", "preliminaryLetterDescription", "/FRA",
            "Invoice_preliminaryLetterDescription_FRA.docFragment.ftl") {
        public FixtureScript script() {
            // subclasses are necessary because
            // FixtureScriptsSpecificationProvider's MultipleExecutionPolicy set to ONCE_PER_CLASS
            return new DocFragmentScript() {};
        }
    },
    InvoiceDescriptionFra(
            "org.estatio.dom.invoice.Invoice", "description", "/FRA",
            "Invoice_description_FRA.docFragment.ftl") {
        public FixtureScript script() {
            // subclasses are necessary because
            // FixtureScriptsSpecificationProvider's MultipleExecutionPolicy set to ONCE_PER_CLASS
            return new DocFragmentScript() {};
        }
    },

    InvoiceItemDescriptionFra(
            "org.estatio.dom.lease.invoicing.InvoiceItemForLease", "description", "/FRA",
            "InvoiceItem_description_FRA.docFragment.ftl") {
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

            final String templateText = getTemplateText();
            if(docFrag != null && Objects.equals(docFrag.getAtPath(), atPath)) {
                docFrag.setTemplateText(templateText);
            } else {
                docFragmentRepository.create(objectType, name, atPath, templateText);
            }
        }

        @Inject
        DocFragmentRepository docFragmentRepository;
    }

    public abstract FixtureScript script();

    public static String read(final String resourceName) {
        try {
            return Resources.toString(Resources.getResource(DocFragmentData.class, resourceName), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
