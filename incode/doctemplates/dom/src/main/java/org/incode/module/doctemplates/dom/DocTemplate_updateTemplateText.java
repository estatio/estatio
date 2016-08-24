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
package org.incode.module.doctemplates.dom;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin
public class DocTemplate_updateTemplateText {

    //region > constructor
    private final DocTemplate docTemplate;

    public DocTemplate_updateTemplateText(final DocTemplate docTemplate) {
        this.docTemplate = docTemplate;
    }
    //endregion


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public DocTemplate $$(
            @ParameterLayout(named = "Text", multiLine = 14)
            final String templateText
    ) {
        docTemplate.setTemplateText(templateText);
        return docTemplate;
    }

    public String default0$$() {
        return docTemplate.getTemplateText();
    }
    

}
