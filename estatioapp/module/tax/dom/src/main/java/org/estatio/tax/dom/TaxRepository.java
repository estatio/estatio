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
package org.estatio.tax.dom;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Collections2;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Tax.class)
public class TaxRepository extends UdoDomainRepositoryAndFactory<Tax> {

    public TaxRepository() {
        super(TaxRepository.class, Tax.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public Tax newTax(
            final String reference,
            final String name,
            // TODO: should probably be asking for the country here, but Tax has no dependency on country module at the moment.
            final ApplicationTenancy applicationTenancy) {

        final Tax tax = newTransientInstance();
        tax.setReference(reference);
        tax.setName(name);
        tax.setApplicationTenancyPath(applicationTenancy.getPath());
        persist(tax);
        return tax;
    }

    @Programmatic
    public List<Tax> allTaxes() {
        return allInstances();
    }

    @Programmatic
    public Tax findByReference(final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Tax findOrCreate(final String reference, final String name, final ApplicationTenancy applicationTenancy) {
        Tax tax =  findByReference(reference);
        if (tax == null) {
            tax = newTax(reference, name, applicationTenancy);
        }
        return tax;
    }

    @Programmatic
    public Collection<Tax> findByApplicationTenancy(final ApplicationTenancy applicationTenancy) {
        return Collections2.filter(allInstances(), tax -> tax.getApplicationTenancy().equals(applicationTenancy));
    }



}
