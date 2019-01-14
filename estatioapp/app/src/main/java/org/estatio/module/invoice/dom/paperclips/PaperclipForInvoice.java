/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.module.invoice.dom.paperclips;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.mixins.T_documents;

import org.estatio.module.invoice.dom.Invoice;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
            /*
            select distinct i.invoiceNumber, i.invoiceDate
              from Invoice i
              join PaperclipForInvoice PFI on i.id = PFI.invoiceId
              join incodeDocuments.Paperclip p on PFI.id = p.id
              join incodeDocuments.DocumentAbstract d on p.documentId = d.id
              join incodeDocuments.DocumentType dt on d.typeId = dt.id
             where 1=1
               and dt.reference in ('SUPPLIER-RECEIPT','TAX-REGISTER')
               and i.invoiceDate >= '1-Jan-2018' and i.invoiceDate < '1-Jan-2019'
            */
        @javax.jdo.annotations.Query(
                name = "findInvoicesByInvoiceDateBetweenWithSupportingDocuments", language = "JDOQL",
                value = "SELECT DISTINCT invoice " +
                        "  FROM org.estatio.module.invoice.dom.paperclips.PaperclipForInvoice " +
                        " WHERE (document.type.reference == 'TAX-REGISTER' || document.type.reference == 'SUPPLIER-RECEIPT') " +
                        "    && invoice.invoiceDate >= :invoiceDateFrom " +
                        "    && invoice.invoiceDate <  :invoiceDateTo " +
                        " ORDER BY invoice.invoiceNumber"),
})
@DomainObject(
        objectType = "org.estatio.dom.invoice.paperclips.PaperclipForInvoice"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class PaperclipForInvoice extends Paperclip {


    @Column(
            allowsNull = "false",
            name = "invoiceId"
    )
    @Getter @Setter
    private Invoice invoice;



    @NotPersistent
    @Override
    public Object getAttachedTo() {
        return getInvoice();
    }
    @Override
    protected void setAttachedTo(final Object object) {
        setInvoice((Invoice) object);
    }



    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider extends PaperclipRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(Invoice.class, PaperclipForInvoice.class);
        }
    }


    @Mixin
    public static class _documents extends T_documents<Invoice> {
        public _documents(final Invoice invoice) {
            super(invoice);
        }
    }

}
