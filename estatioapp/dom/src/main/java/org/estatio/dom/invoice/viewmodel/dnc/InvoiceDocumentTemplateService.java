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
package org.estatio.dom.invoice.viewmodel.dnc;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.mixins.T_createDocumentAndRender;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.Invoice_createDocumentAndScheduleRender;

@DomainService(nature = NatureOfService.DOMAIN)
public class InvoiceDocumentTemplateService {

    List<DocumentTemplate> templatesFor(final Invoice invoice) {
        return Lists.newArrayList(
                createDocumentMixin(invoice)
                        .choices0$$()
                        .stream()
                        .filter(x -> !x.isPreviewOnly())
                        .collect(Collectors.toList()));
    }

    Document createAttachAndScheduleRender(final Invoice invoice, final DocumentTemplate documentTemplate1) throws
            IOException {
        final Invoice_createDocumentAndScheduleRender mixin = createDocumentMixin(invoice);
        return (Document) mixin.$$(documentTemplate1, T_createDocumentAndRender.Intent.CREATE_AND_ATTACH);
    }

    private Invoice_createDocumentAndScheduleRender createDocumentMixin(final Invoice anyInvoice) {
        return factoryService.mixin(Invoice_createDocumentAndScheduleRender.class, anyInvoice);
    }



    @Inject
    FactoryService factoryService;

}
