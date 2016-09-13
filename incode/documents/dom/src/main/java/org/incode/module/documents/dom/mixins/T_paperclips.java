/*
 *
 *  Copyright 2015 incode.org
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
package org.incode.module.documents.dom.mixins;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.links.Paperclip;
import org.incode.module.documents.dom.impl.links.PaperclipRepository;

public abstract class T_paperclips<T> {

    //region > constructor and mixedIn accessor
    private final T attachedTo;
    public T_paperclips(final T attachedTo) {
        this.attachedTo = attachedTo;
    }

    @Programmatic
    public T getAttachedTo() {
        return attachedTo;
    }

    //endregion

    //region > $$
    public static class DomainEvent extends DocumentsModule.ActionDomainEvent<T_paperclips>  { }
    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @CollectionLayout(defaultView = "table")
    public List<Paperclip> $$() {
        return paperclipRepository.findByAttachedTo(attachedTo);
    }
    //endregion

    //region > injected services
    @Inject
    PaperclipRepository paperclipRepository;
    //endregion

}
