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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.invoice.dom.Invoice;

import lombok.Getter;
import lombok.Setter;

public abstract class DocAndCommAbstract<T extends DocAndCommAbstract<T>> {

    //region > title, icon etc
    public String title() {
        return titleService.titleOf(getInvoice().getBuyer());
    }
    //endregion

    @Property
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    @MemberOrder(sequence = "1") // workaround, DocAndCommForInvoiceDoc.layout.xml not being honoured for table, for some reason
    private Invoice invoice;

    @Property()
    @MemberOrder(sequence = "4") // workaround, DocAndCommForInvoiceDoc.layout.xml not being honoured for table, for some reason
    @Getter @Setter
    private CommunicationChannel sendTo;

    public DocAndCommAbstract() {
    }

    public DocAndCommAbstract(final Invoice invoice) {
        this.invoice = invoice;
        this.sendTo = invoice.getSendTo();
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Factory {

        public static interface DncProvider<T extends DocAndCommAbstract<T>> {
            T instantiate(Invoice<?> invoice);
        }

        <T extends DocAndCommAbstract<T>>  List<T> documentsAndCommunicationsFor(
                final List<Invoice<?>> invoices,
                final DncProvider<T> provider) {
            return Lists.newArrayList(
                    invoices.stream()
                            .map(invoice -> provider.instantiate(invoice))
                            .collect(Collectors.toList())
            );
        }

        public <T extends DocAndCommAbstract<T>> List<T> documentsAndCommunicationsFor(
                final Invoice<?> invoice,
                final DncProvider<T> provider) {
            return documentsAndCommunicationsFor(Collections.singletonList(invoice), provider);
        }

    }


    @Inject
    TitleService titleService;

}
