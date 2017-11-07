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
package org.estatio.module.invoice.dom;

import java.util.List;

import org.datanucleus.query.typesafe.TypesafeQuery;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Invoice.class)
public class InvoiceRepository extends UdoDomainRepositoryAndFactory<Invoice> {

    public InvoiceRepository() {
        super(InvoiceRepository.class, Invoice.class);
    }



    @Programmatic
    public List<Invoice> findByStatus(
            final InvoiceStatus status) {
        return allMatches("findByStatus",
                "status", status);
    }

    @Programmatic
    public List<Invoice> findByBuyer(final Party party) {
        return allMatches("findByBuyer",
                "buyer", party);
    }

    @Programmatic
    public List<Invoice> findBySeller(final Party party) {
        return allMatches("findBySeller",
                "seller", party);
    }

    @Programmatic
    public List<Invoice> findMatchingInvoiceNumber(final String invoiceNumber) {
        return allMatches("findMatchingInvoiceNumber",
                "invoiceNumber", invoiceNumber);
    }



    // //////////////////////////////////////

    @Programmatic
    public List<Invoice> allInvoices() {
        return allInstances();
    }

    @Programmatic
    public List<Invoice> findBySendTo(final CommunicationChannel communicationChannel) {
        final TypesafeQuery<Invoice> query = isisJdoSupport.newTypesafeQuery(Invoice.class);
        QInvoice cand = QInvoice.candidate();
        return query.filter(cand.sendTo.eq(communicationChannel)).executeList();
    }

    // //////////////////////////////////////


    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;


}
