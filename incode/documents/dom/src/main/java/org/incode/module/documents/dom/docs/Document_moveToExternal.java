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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;

public class Document_moveToExternal {

    //region > constructor
    private final Document document;

    public Document_moveToExternal(final Document document) {
        this.document = document;
    }
    //endregion


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Document $$(
            @ParameterLayout(named = "External url")
            final String externalUrl,
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "New name")
            final String name,
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = DocumentsModule.JdoColumnLength.MIME_TYPE)
            @ParameterLayout(named = "New mime type")
            final String mimeType
        ) {

        document.setExternalUrl(externalUrl);
        document.setBlobBytes(null);
        document.setClobChars(null);
        document.setSort(DocumentSort.EXTERNAL_BLOB);

        if(name != null) {
            document.setName(name);
        }
        if(mimeType != null) {
            document.setMimeType(mimeType);
        }

        return document;
    }


}
