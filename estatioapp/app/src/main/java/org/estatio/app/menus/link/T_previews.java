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
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.documents.dom.docs.DocumentTemplate;

public abstract class T_previews<T extends WithApplicationTenancy> extends T_documentTemplates<T> {

    public T_previews(final T domainObject, final String... docTypes) {
        super(domainObject, docTypes);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public URL $$(final DocumentTemplate template) throws IOException {
        final StringInterpolatorService.Root root = newRoot();
        return template.preview(root, null);
    }

    public boolean hide$$() {
        return choices0$$().isEmpty();
    }

    public List<DocumentTemplate> choices0$$() {
        return documentTemplatesFor(intent);
    }

    @Override
    protected boolean canAccept(final DocumentTemplate template) {
        return template.getRenderingStrategy().isPreviewsToUrl();
    }

}
