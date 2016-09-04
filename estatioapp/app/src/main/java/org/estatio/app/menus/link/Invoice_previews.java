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
package org.estatio.app.menus.link;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.invoice.Invoice;
import org.estatio.fixture.documents.DocumentTypeAndTemplatesForItalianInvoicesUsingSsrs;

@Mixin
public class Invoice_previews extends T_previews<Invoice> {

    public Invoice_previews(final Invoice invoice) {
        // hard-coded list of candidate doc types (equivalent to class names in LinkRefDat entity, see LinkRefData fixture)
        super(invoice,
                DocumentTypeAndTemplatesForItalianInvoicesUsingSsrs.DOC_TYPE_REF_INVOICE_PRELIM_ITA,
                DocumentTypeAndTemplatesForItalianInvoicesUsingSsrs.DOC_TYPE_REF_INVOICE_ITA);
    }



}
