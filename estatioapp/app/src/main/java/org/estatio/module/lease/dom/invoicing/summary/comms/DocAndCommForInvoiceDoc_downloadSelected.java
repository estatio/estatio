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
package org.estatio.module.lease.dom.invoicing.summary.comms;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.invoice.dom.DocumentTypeData;

/**
 * TODO: inline this mixin (including superclass functionality) ... too confusing/abstracted
 */
@Mixin(method = "act")
public class DocAndCommForInvoiceDoc_downloadSelected extends DocAndCommAbstract_downloadSelected<DocAndCommForInvoiceDoc> {

    public DocAndCommForInvoiceDoc_downloadSelected(final DocAndCommForInvoiceDoc docAndComm) {
        super(docAndComm, DocumentTypeData.INVOICE);
    }

}
