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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.types.NameType;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Documents",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "77.2")
public class DocumentTypeMenu extends UdoDomainService<DocumentTypeMenu> {

    public DocumentTypeMenu() {
        super(DocumentTypeMenu.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "1")
    public DocumentType newDocumentType(
            @Parameter(
                    regexPattern = org.incode.module.base.dom.types.ReferenceType.Meta.REGEX,
                    regexPatternReplacement = org.incode.module.base.dom.types.ReferenceType.Meta.REGEX_DESCRIPTION,
                    maxLength = DocumentType.ReferenceType.Meta.MAX_LEN
            )
            @ParameterLayout(named = "Reference")
            final String reference,
            @Parameter(maxLength = NameType.Meta.MAX_LEN)
            @ParameterLayout(named = "Name")
            final String name) {

        return documentTypeRepository.create(reference, name);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<DocumentType> allDocumentTypes() {
        return documentTypeRepository.allTypes();
    }


    // //////////////////////////////////////

    @Inject
    private DocumentTypeRepository documentTypeRepository;


}
