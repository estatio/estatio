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
package org.incode.module.documents.dom.docs;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.documents.dom.DocumentsModule;

@Mixin
public class Document_movedToExternalUrl {


    //region > constructor
    private final Document document;

    public Document_movedToExternalUrl(final Document document) {
        this.document = document;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<Document_movedToExternalUrl> { }
    /**
     * The idea is that this would be called by a background process.  This is a prototyping action, for demo (or to
     * call programmatically by said background service).
     *
     * <p>
     *     Not yet tackled in this design is how to obtain the content of the service (what if there are credentials
     *     etc that need to be provided.  However this document object <i>does</i> store is the
     *     {@link Document#getSort() document sort} and {@link Document#getMimeType() mime type}  which lets us know how to interpret the remotely held data.
     * </p>
     */
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(named = "Moved to External URL")
    public Document $$(
            @ParameterLayout(named = "External URL")
            final String externalUrl,
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Name")
            final String name
    ) {
        document.setExternalUrl(externalUrl);
        document.setBlobBytes(null);
        document.setClobChars(null);

        final DocumentSort sort = document.getSort();
        document.setSort(sort.asExternal());

        if(name != null) {
            document.setName(name);
        }

        return document;
    }

    public boolean hide$$() {
        return document.getSort().isExternal();
    }

    public String default1$$() {
        return document.getName();
    }




    @Inject
    MessageService messageService;

}
