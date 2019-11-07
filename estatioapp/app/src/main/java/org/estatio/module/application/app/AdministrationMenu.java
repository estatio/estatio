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
package org.estatio.module.application.app;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.registry.ServiceRegistry;
import org.apache.isis.core.metamodel.spec.ObjectSpecId;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;

import org.estatio.module.base.dom.UdoDomainService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.admin.AdministrationMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY,
        menuOrder = "10.1"
)
public class AdministrationMenu extends UdoDomainService<AdministrationMenu> {

    public AdministrationMenu() {
        super(AdministrationMenu.class);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3.5")
    public AdminDashboard openAdminDashboard() {
        return serviceRegistry.injectServicesInto(new AdminDashboard());
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3.6")
    public Object lookupObject(
            final String objectType,
            final String identifier) {
        return bookmarkService.lookup(new Bookmark(objectType, identifier));
    }
    public List<String> choices0LookupObject() {
        return specificationLookup.allSpecifications().stream()
                .filter(ObjectSpecification::isPersistenceCapable)
                .map(ObjectSpecification::getSpecId)
                .map(ObjectSpecId::asString)
                .sorted()
                .collect(Collectors.toList());
    }


    @Inject
    ServiceRegistry serviceRegistry;

    @javax.inject.Inject
    SpecificationLoader specificationLookup;



}
