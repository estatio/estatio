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
package org.incode.module.documents.dom.impl.paperclips;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;

@Mixin
public class Paperclip_changeRole {

    //region > constructor
    private final Paperclip paperclip;

    public Paperclip_changeRole(final Paperclip paperclip) {
        this.paperclip = paperclip;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<Paperclip_changeRole>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public Paperclip $$(
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Document role")
            final String roleName
    ) {
        paperclip.setRoleName(roleName);
        return paperclip;
    }

    public String default0$$() {
        return paperclip.getRoleName();
    }



}
