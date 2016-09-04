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
import java.net.URL;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.docs.DocumentTemplate;

import org.estatio.dom.asset.Property;
import org.estatio.fixture.documents.DocumentTypeAndTemplateForDemoSsrsOnProperty;

@Mixin
public class Property_previews extends T_previews<Property> {

    public Property_previews(final Property property) {
        // hard-coded list of candidate doc types (equivalent to class names in LinkRefDat entity, see LinkRefData fixture)
        super(property, DocumentTypeAndTemplateForDemoSsrsOnProperty.DEMO_SSRS_GLOBAL);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @Override
    public URL $$(final DocumentTemplate template) throws IOException {
        return super.$$(template);
    }
}
