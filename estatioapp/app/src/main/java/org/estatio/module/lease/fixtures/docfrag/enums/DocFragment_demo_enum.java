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
package org.estatio.module.lease.fixtures.docfrag.enums;

import java.io.IOException;
import java.util.Objects;

import com.google.common.io.Resources;

import org.apache.commons.io.Charsets;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.docfragment.dom.impl.DocFragment;
import org.incode.module.docfragment.dom.impl.DocFragmentRepository;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.lease.fixtures.docfrag.builders.DocFragmentBuilder;
import org.estatio.module.lease.seed.DocFragmentData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum DocFragment_demo_enum
        implements PersonaWithBuilderScript<DocFragment, DocFragmentBuilder>, PersonaWithFinder<DocFragment> {

    //
    // using ITA's fragments for now
    //
    InvoicePreliminaryLetterDescription_DemoGbr(
            "org.estatio.dom.lease.invoicing.ssrs.InvoiceAttributesVM", "preliminaryLetterDescription",
            ApplicationTenancy_enum.Gb,
            "Invoice_preliminaryLetterDescription_ITA.docFragment.ftl"),
    InvoiceDescription_DemoGbr(
            "org.estatio.dom.lease.invoicing.ssrs.InvoiceAttributesVM", "description", ApplicationTenancy_enum.Gb,
            "Invoice_description_ITA.docFragment.ftl"),
    InvoiceItemDescription_DemoGbr(
            "org.estatio.dom.lease.invoicing.ssrs.InvoiceItemAttributesVM", "description", ApplicationTenancy_enum.Gb,
            "InvoiceItem_description_ITA.docFragment.ftl"),

    //
    // using ITA's fragments for now
    //
    InvoicePreliminaryLetterDescription_DemoNld(
            "org.estatio.dom.lease.invoicing.ssrs.InvoiceAttributesVM", "preliminaryLetterDescription", ApplicationTenancy_enum.Nl,
            "Invoice_preliminaryLetterDescription_ITA.docFragment.ftl"),
    InvoiceDescription_DemoNld(
            "org.estatio.dom.lease.invoicing.ssrs.InvoiceAttributesVM", "description", ApplicationTenancy_enum.Nl,
            "Invoice_description_ITA.docFragment.ftl"),
    InvoiceItemDescription_DemoNld(
            "org.estatio.dom.lease.invoicing.ssrs.InvoiceItemAttributesVM", "description", ApplicationTenancy_enum.Nl,
            "InvoiceItem_description_ITA.docFragment.ftl"),
    ;

    private final String objectType;
    private final String name;
    private final ApplicationTenancy_enum applicationTenancy_d;
    private final String templateResourceName;

    String getTemplateText() {
        return read(templateResourceName);
    }

    public static String read(final String resourceName) {
        try {
            return Resources.toString(Resources.getResource(DocFragmentData.class, resourceName), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override public DocFragment findUsing(final ServiceRegistry2 serviceRegistry) {
        final DocFragmentRepository repo = serviceRegistry.lookupService(DocFragmentRepository.class);
        final DocFragment docFrag = repo.findByObjectTypeAndNameAndApplicableToAtPath(objectType, name, applicationTenancy_d.getPath());
        return docFrag != null && Objects.equals(docFrag.getAtPath(), applicationTenancy_d.getPath())
                ? docFrag
                : null;
    }
    @Override
    public DocFragmentBuilder builder() {
        return new DocFragmentBuilder()
                .setName(name)
                .setObjectType(objectType)
                .setAtPath(applicationTenancy_d.getPath())
                .setTemplateText(getTemplateText());
    }


}
