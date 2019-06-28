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

package org.estatio.module.invoicegroup.app;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.invoicegroup.dom.InvoiceGroup;
import org.estatio.module.invoicegroup.dom.InvoiceGroupRepository;

@DomainService(
        nature = NatureOfService.VIEW, objectType = "invoicegroup.InvoiceGroupMenu"
)
public class InvoiceGroupMenu {


    @Action(semantics = SemanticsOf.SAFE)
    public InvoiceGroup createInvoiceGroup(final String reference, final String name) {
        return invoiceGroupRepository.createInvoiceGroup(reference, name);
    }
    public String validate0Reference(final String reference) {
        return invoiceGroupRepository.findByReference(reference)
                .map(x -> "Reference already in use")
                .orElse(null);
    }



    @Action(semantics = SemanticsOf.SAFE)
    public InvoiceGroup findInvoiceGroup(final String reference) {
        return invoiceGroupRepository.findByReference(reference).orElse(null);
    }



    @Action(semantics = SemanticsOf.SAFE)
    public List<InvoiceGroup> allInvoiceGroups() {
        return invoiceGroupRepository.allInvoiceGroups();
    }

    @Inject
    InvoiceGroupRepository invoiceGroupRepository;


}
