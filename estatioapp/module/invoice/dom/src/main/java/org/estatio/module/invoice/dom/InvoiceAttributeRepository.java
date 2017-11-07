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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = InvoiceAttribute.class)
public class InvoiceAttributeRepository extends UdoDomainRepositoryAndFactory<InvoiceAttribute> {

    public InvoiceAttributeRepository() {
        super(InvoiceAttributeRepository.class, InvoiceAttribute.class);
    }

    @Programmatic
    public List<InvoiceAttribute> findByInvoice(
            final Invoice invoice) {
        return allMatches("findByInvoice",
                "invoice", invoice);
    }

    @Programmatic
    public InvoiceAttribute findByInvoiceAndName(
            final Invoice invoice,
            final InvoiceAttributeName name) {
        return uniqueMatch("findByInvoiceAndName",
                "invoice", invoice, "name", name);
    }

    @Programmatic
    public InvoiceAttribute newAttribute(
            final Invoice invoice,
            final InvoiceAttributeName name,
            final String value,
            final boolean overridden) {
        InvoiceAttribute invoiceAttribute = newTransientInstance();
        invoiceAttribute.setInvoice(invoice);
        invoiceAttribute.setName(name);
        invoiceAttribute.setValue(value);
        invoiceAttribute.setOverridden(overridden);
        persistIfNotAlready(invoiceAttribute);
        return invoiceAttribute;
    }

}
