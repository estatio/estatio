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

package org.estatio.module.invoicegroup.dom;

import java.util.List;
import java.util.Optional;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.apache.isis.applib.query.QueryDefault;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = InvoiceGroup.class)
public class InvoiceGroupRepository extends UdoDomainRepositoryAndFactory<InvoiceGroup> {

    public InvoiceGroupRepository() {
        super(InvoiceGroupRepository.class, InvoiceGroup.class);
    }

    public InvoiceGroup upsert(final String reference, final String description) {
        return findByReference(reference)
                .orElseGet(() -> createInvoiceGroup(reference, description));
    }

    public InvoiceGroup createInvoiceGroup(final String reference, final String name) {
        return repositoryService.persistAndFlush(new InvoiceGroup(reference, name));
    }

    public Optional<InvoiceGroup> findByReference(final String reference) {
        List<InvoiceGroup> list = repositoryService.allMatches(new QueryDefault<>(InvoiceGroup.class,
                "findByReference", "reference", reference));
        return list.isEmpty() ? Optional.ofNullable(null) : Optional.of(list.get(0));
    }

    public Optional<InvoiceGroup> findContainingProperty(final Property property) {
        List<InvoiceGroup> list = repositoryService.allMatches(new QueryDefault<>(InvoiceGroup.class,
                "findContainingProperty", "property", property));
        return list.isEmpty() ? Optional.ofNullable(null) : Optional.of(list.get(0));
    }

    @Programmatic
    public List<InvoiceGroup> allInvoiceGroups() {
        return repositoryService.allInstances(InvoiceGroup.class);
    }

}
