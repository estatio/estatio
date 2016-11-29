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
package org.estatio.app.menus.numerator;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.numerator.dom.impl.Numerator;
import org.estatio.numerator.dom.impl.NumeratorRepository;

@DomainService(menuOrder = "80", nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Administration",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "120.1"
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
