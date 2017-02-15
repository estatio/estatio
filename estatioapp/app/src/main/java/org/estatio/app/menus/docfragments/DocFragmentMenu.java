/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.app.menus.docfragments;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.docfragment.dom.impl.DocFragment;
import org.incode.module.docfragment.dom.impl.DocFragmentRepository;
import org.incode.module.docfragment.dom.types.TemplateTextType;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.docfragments.DocFragmentMenu",
        repositoryFor = DocFragment.class
)
@DomainServiceLayout(
        named = "Other",
        menuOrder = "99"
)
public class DocFragmentMenu {


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public DocFragment newDocFragment(
            @ParameterLayout(named="Object type")
            final String objectType,
            @ParameterLayout(named="Name")
            final String name,
            final ApplicationTenancy applicationTenancy,
            @ParameterLayout(named="Template text", multiLine = TemplateTextType.Meta.MULTILINE)
            final String templateText
            ) {
        return docfragmentRepository.create(objectType, name, applicationTenancy.getPath(), templateText);
    }

    public List<String> choices0NewDocFragment() {
        final Collection<ObjectSpecification> objectSpecifications = specificationLookup.allSpecifications();
        return Lists.newArrayList(
                FluentIterable.from(objectSpecifications)
                        .filter(x -> !(x.isValue() || x.isService() || x.isAbstract() || x.isMixin() || x.isParentedOrFreeCollection()))
                .transform(x -> x.getSpecId().asString())
                .toSortedList(String::compareTo)
        );
    }

    public List<ApplicationTenancy> choices2NewDocFragment() {
        return estatioApplicationTenancyRepository.allTenancies();
    }




    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "2")
    public List<DocFragment> allDocFragments() {
        return docfragmentRepository.listAll();
    }



    @javax.inject.Inject
    DocFragmentRepository docfragmentRepository;

    @javax.inject.Inject
    SpecificationLoader specificationLookup;

    @javax.inject.Inject EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;


}
