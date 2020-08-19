/*
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
package org.estatio.module.capex.dom.invoice;

import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceQueryHelperRepo extends UdoDomainRepositoryAndFactory<IncomingInvoiceQueryHelper> {

    public IncomingInvoiceQueryHelperRepo() {
        super(IncomingInvoiceQueryHelperRepo.class, IncomingInvoiceQueryHelper.class);
    }

    @Programmatic
    public List<IncomingInvoiceQueryHelper> findByInvoiceItemReportedDate(
            final LocalDate invoiceItemReportedDate) {
        return allMatches("findByInvoiceItemReportedDate",
                "invoiceItemReportedDate", invoiceItemReportedDate);
    }

    @Programmatic
    public List<IncomingInvoiceQueryHelper> findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPathsContains(
            final String invoiceItemFixedAssetReference,
            final IncomingInvoiceType invoiceItemType,
            final LocalDate invoiceItemReportedDate,
            final List<String> atPaths) {
        if (atPaths==null || atPaths.isEmpty()) return Collections.emptyList();
        return allMatches("findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath",
                "invoiceItemFixedAssetReference", invoiceItemFixedAssetReference,
                "invoiceItemType", invoiceItemType,
                "invoiceItemReportedDate", invoiceItemReportedDate,
                "atPaths", atPaths);
    }

    @Programmatic
    public List<IncomingInvoiceQueryHelper> findByFixedAssetReferenceAndReportedDateAndBuyerAtPathContains(
            final String invoiceItemFixedAssetReference,
            final LocalDate invoiceItemReportedDate,
            final List<String> atPaths) {
        if (atPaths==null || atPaths.isEmpty()) return Collections.emptyList();
        return allMatches("findByFixedAssetReferenceAndReportedDateAndBuyerAtPath",
                "invoiceItemFixedAssetReference", invoiceItemFixedAssetReference,
                "invoiceItemReportedDate", invoiceItemReportedDate,
                "atPaths", atPaths);
    }

}
