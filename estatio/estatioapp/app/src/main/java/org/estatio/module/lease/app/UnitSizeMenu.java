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
package org.estatio.module.lease.app;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.lease.dom.occupancy.tags.UnitSize;

@DomainService(
        repositoryFor = UnitSize.class,
        objectType = "org.estatio.app.menus.brand.UnitSizeMenu"
)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "900.10"
)
public class UnitSizeMenu extends UdoDomainRepositoryAndFactory<UnitSize> {

    public UnitSizeMenu() {
        super(UnitSizeMenu.class, UnitSize.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public UnitSize newUnitSize(final String unitSizeName) {
        UnitSize unitSize = newTransientInstance(UnitSize.class);
        unitSize.setName(unitSizeName);
        persist(unitSize);
        return unitSize;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<UnitSize> allUnitSizes() {
        return allInstances();
    }

}
