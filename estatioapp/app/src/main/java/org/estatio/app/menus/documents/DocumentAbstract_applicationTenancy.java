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
package org.estatio.app.menus.documents;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.documents.dom.impl.docs.DocumentAbstract;

@Mixin
public class DocumentAbstract_applicationTenancy {

    //region > constructor
    private final DocumentAbstract documentAbstract;

    public DocumentAbstract_applicationTenancy(final DocumentAbstract documentAbstract) {
        this.documentAbstract = documentAbstract;
    }

    //endregion


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public ApplicationTenancy $$()  {
        return applicationTenancyRepository.findByPath(documentAbstract.getAtPath());
    }
    

    //region > injected services

    @javax.inject.Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
    //endregion

}
