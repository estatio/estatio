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
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.DocumentsModule;

@Mixin
public class DocumentAbstract_downloadTextAsClob {

    //region > constructor
    private final DocumentAbstract<?> document;

    public DocumentAbstract_downloadTextAsClob(final DocumentAbstract<?> document) {
        this.document = document;
    }
    //endregion

    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<DocumentAbstract_downloadTextAsClob>  { }
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(named = "Download")
    public Clob $$() {
        return new Clob(document.getName(), document.getMimeType(), document.getText());
    }

    public boolean hide$$() {
        return document.getSort() != DocumentSort.TEXT;
    }


}
