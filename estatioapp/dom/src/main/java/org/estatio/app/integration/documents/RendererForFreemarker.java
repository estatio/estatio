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
package org.estatio.app.integration.documents;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.module.documents.dom.rendering.RendererFromCharsToChars;
import org.incode.module.documents.dom.types.DocumentType;

import freemarker.template.TemplateException;

public class RendererForFreemarker implements RendererFromCharsToChars {

    public String renderCharsToChars(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel,
            final String documentName) throws IOException {

        try {
            return freeMarkerService.render(documentType.getReference(), atPath, templateVersion, templateChars, dataModel);
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }


    @Inject
    private ClockService clockService;
    @Inject
    private FreeMarkerService freeMarkerService;

}
