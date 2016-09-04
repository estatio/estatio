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
package org.estatio.app.menus.link;

import java.io.IOException;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.DocumentAbstract;
import org.incode.module.documents.dom.docs.DocumentTemplate;

import org.estatio.dom.asset.Property;
import org.estatio.fixture.documents.DocumentTypeAndTemplateForDemoSsrsOnProperty;

@Mixin
public class Property_reports extends T_reports<Property> {

    public Property_reports(final Property property) {
        // hard-coded list of candidate doc types (equivalent to class names in LinkRefDat entity, see LinkRefData fixture)
        super(property, DocumentTypeAndTemplateForDemoSsrsOnProperty.DEMO_SSRS_GLOBAL);
    }

    @Override
    protected String documentNameOf(
            final Property domainObject, final DocumentTemplate template, final String documentName) {
        return super.documentNameOf(domainObject, template, documentName) + ".pdf";
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @Override public DocumentAbstract $$(
            final DocumentTemplate template,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.NAME, optionality = Optionality.OPTIONAL) @ParameterLayout(named = "Document name") final String documentName,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.NAME, optionality = Optionality.OPTIONAL) @ParameterLayout(named = "Role name") final String roleName)
            throws IOException {
        return super.$$(template, documentName, roleName);
    }
}
