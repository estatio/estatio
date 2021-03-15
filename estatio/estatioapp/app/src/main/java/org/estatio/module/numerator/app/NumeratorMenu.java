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
package org.estatio.module.numerator.app;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;

@DomainService(
        menuOrder = "80",
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.numerator.NumeratorMenu"
)
@DomainServiceLayout(
        named = "Other", menuBar = DomainServiceLayout.MenuBar.PRIMARY, menuOrder = "900.14"
)
public class NumeratorMenu extends UdoDomainRepositoryAndFactory<Numerator> {

    public NumeratorMenu() {
        super(NumeratorMenu.class, Numerator.class);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<Numerator> allNumerators() {
        return numeratorRepository.allNumerators();
    }

    @Inject
    NumeratorRepository numeratorRepository;

}
