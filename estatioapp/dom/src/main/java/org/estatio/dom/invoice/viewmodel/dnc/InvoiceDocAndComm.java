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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.ViewModel;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.estatio.dom.invoice.Invoice;

import lombok.Getter;
import lombok.Setter;

@ViewModel
public class InvoiceDocAndComm {

    @Title
    @Property()
    @Getter @Setter
    private Invoice invoice;

    @Property()
    @Getter @Setter
    private CommunicationChannel sendTo;

    public InvoiceDocAndComm() {
    }

    public InvoiceDocAndComm(final Invoice invoice) {
        this.invoice = invoice;
        this.sendTo = invoice.getSendTo();
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Factory {

        List<InvoiceDocAndComm> documentsAndCommunicationsFor(
                final List<Invoice> invoices) {
            return Lists.newArrayList(
                    invoices.stream()
                            .map(invoice -> new InvoiceDocAndComm(invoice))
                            .collect(Collectors.toList())
            );
        }

        List<InvoiceDocAndComm> documentsAndCommunicationsFor(final Invoice invoice) {
            return documentsAndCommunicationsFor(Collections.singletonList(invoice));
        }
    }

}
