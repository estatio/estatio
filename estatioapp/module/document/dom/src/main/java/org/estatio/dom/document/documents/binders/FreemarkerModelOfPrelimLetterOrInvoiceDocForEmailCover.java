/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.document.documents.binders;

import java.util.Optional;

import javax.inject.Inject;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.party.dom.Party;

import lombok.Data;

public class FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover
        extends RendererModelFactoryAbstract<Document> {

    public FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover() {
        super(Document.class);
    }

    @Override
    protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate,
            final Document prelimLetterOrInvoiceNoteDoc) {

        boolean isPrimaryType = DocumentTypeData.isPrimaryType(prelimLetterOrInvoiceNoteDoc);
        if (!isPrimaryType) {
            final String docTypeRef2 = prelimLetterOrInvoiceNoteDoc.getType().getReference();
            throw new IllegalArgumentException(String.format(
                    "Document must be a prelim letter or invoice (provided document's type is '%s')", docTypeRef2));
        }

        final InvoiceForLease invoice = paperclipRepository.paperclipAttaches(prelimLetterOrInvoiceNoteDoc, InvoiceForLease.class);

        final DataModel dataModel = new DataModel();
        dataModel.setInvoice(invoice);
        dataModel.setTenant(invoice.getBuyer());
        dataModel.setProperty(invoice.getLease().getProperty());
        final Optional<Occupancy> occupancyIfAny = invoice.getLease().primaryOccupancy();
        if(occupancyIfAny.isPresent()) {
            final Occupancy occupancy = occupancyIfAny.get();
            final Unit unit = occupancy.getUnit();
            dataModel.setUnit(unit);
            dataModel.setBrand(occupancy.getBrand());
        }

        dataModel.setDocument(prelimLetterOrInvoiceNoteDoc);

        return dataModel;
    }

    @Data
    public static class DataModel {
        Invoice invoice;
        Party tenant;
        Property property;
        Unit unit;
        Brand brand;
        Document document;
    }

    @Inject
    PaperclipRepository paperclipRepository;
}
