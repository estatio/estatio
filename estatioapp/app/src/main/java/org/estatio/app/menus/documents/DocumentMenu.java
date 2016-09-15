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

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentRepository;

import org.estatio.dom.UdoDomainService;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Documents",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "77.1")
public class DocumentMenu extends UdoDomainService<DocumentMenu> {

    public DocumentMenu() {
        super(DocumentMenu.class);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<Document> find(
            final LocalDate startDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate endDate
    ) {
        return documentRepository.findBetween(startDate, endDate);
    }

    public LocalDate default0Find() {
        // one week ago.
        return clockService.now().plusDays(-7);
    }


    @Inject
    private ClockService clockService;

    @Inject
    private DocumentRepository documentRepository;


}
