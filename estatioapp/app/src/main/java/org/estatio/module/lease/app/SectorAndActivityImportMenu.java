/*
 *  Copyright 2016 Dan Haywood
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
package org.estatio.module.lease.app;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.user.UserService;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.lease.imports.SectorAndActivityImportManager;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

import javax.inject.Inject;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.invoice.SectorAndActivityImportMenu"
)
@DomainServiceLayout(
        named = "Other", menuBar = DomainServiceLayout.MenuBar.PRIMARY, menuOrder = "900.1"
)
public class SectorAndActivityImportMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public SectorAndActivityImportManager sectorAndActivityImportManager() {
        return new SectorAndActivityImportManager();
    }

    public boolean hideSectorAndActivityImportManager() {
        Person meAsPerson = personRepository.me();
        if (meAsPerson != null && meAsPerson.hasPartyRoleType(PartyRoleTypeEnum.SECTOR_MAINTAINER.findUsing(partyRoleTypeRepository))) {
            return false;
        }
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser());
    }

    @Inject
    UserService userService;

    @Inject
    PersonRepository personRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

}
