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
package org.estatio.dom.index;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Dflt;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.index.api.IndexCreator;
import org.estatio.dom.index.api.IndexFinder;

@DomainService(repositoryFor = Index.class, nature = NatureOfService.DOMAIN)
public class IndexRepository extends UdoDomainRepositoryAndFactory<Index> implements IndexFinder, IndexCreator{

    public IndexRepository() {
        super(IndexRepository.class, Index.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Index newIndex(
            final @Parameter(regexPattern = RegexValidation.REFERENCE, regexPatternReplacement = RegexValidation.REFERENCE_DESCRIPTION) String reference,
            final String name,
            final ApplicationTenancy applicationTenancy) {
        final Index index = new Index(reference, name, applicationTenancy);
        getContainer().injectServicesInto(index);
        persist(index);
        return index;
    }

    public List<ApplicationTenancy> choices2NewIndex() {
        return estatioApplicationTenancyRepository.countryTenanciesForCurrentUser();
    }

    public ApplicationTenancy default2NewIndex() {
        return Dflt.of(choices2NewIndex());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Index> all() {
        return allInstances();
    }

    @Programmatic
    @Override
    public Index findByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    @Override
    public Index findOrCreateIndex(
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name) {
        Index index = findByReference(reference);
        if (index == null) {
            index = newIndex(reference, name, applicationTenancy);
        }
        return index;
    }

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;
}
