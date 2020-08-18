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
package org.estatio.module.coda.dom.restapi;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.coda.dom.codalink.CodaDocLink;
import org.estatio.module.coda.dom.codalink.CodaDocLinkRepository;
import org.estatio.module.invoice.dom.Invoice;

@DomainService(
        nature = NatureOfService.VIEW_REST_ONLY,
        objectType = "codalink.CodaDocLinkService"
)
public class CodaDocLinkServiceRestApi extends UdoDomainService<CodaDocLinkServiceRestApi> {

    public CodaDocLinkServiceRestApi() {
        super(CodaDocLinkServiceRestApi.class);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public CodaDocLink link(
            final String cmpCode, final String docCode, final String docNum,
            final String invoiceBookmark
            ) {
        final Bookmark bookmark = new Bookmark(invoiceBookmark);
        final Invoice invoice = bookmarkService2.lookup(
                bookmark, BookmarkService2.FieldResetPolicy.DONT_RESET, Invoice.class);
        return codaDocLinkRepository.findOrCreate(cmpCode, docCode, docNum, invoice);
    }



    @Inject
    BookmarkService2 bookmarkService2;


    @Inject
    CodaDocLinkRepository codaDocLinkRepository;

}
