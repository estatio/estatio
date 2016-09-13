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
package org.incode.module.documents.dom.impl.links;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.docs.DocumentTemplateRepository;

@Mixin
public class Paperclip_delete {

    //region > constructor
    private final Paperclip paperclip;

    public Paperclip_delete(final Paperclip paperclip) {
        this.paperclip = paperclip;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<Paperclip_delete>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            domainEvent = ActionDomainEvent.class
    )
    public void $$(
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Document role")
            final String roleName
    ) {
        paperclipRepository.delete(paperclip);
        return;
    }


    @Inject
    private PaperclipRepository paperclipRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
