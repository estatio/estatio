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

import org.isisaddons.module.xdocreport.dom.service.OutputType;
import org.isisaddons.module.xdocreport.dom.service.XDocReportModel;
import org.isisaddons.module.xdocreport.dom.service.XDocReportService;

import org.incode.module.documents.dom.docs.DocumentRepository;
import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.rendering.RendererToBytes;
import org.incode.module.documents.dom.types.DocumentType;

public abstract class RendererForXDocReportAbstract implements RendererToBytes {

    private final OutputType outputType;

    protected RendererForXDocReportAbstract(final OutputType outputType) {
        this.outputType = outputType;
    }

    @Override
    public byte[] renderToBytes(final DocumentTemplate documentTemplate, final Object dataModel)
            throws IOException {

        final DocumentType documentType = documentTemplate.getType();
        final String typeRef = documentType.getReference();
        final String atPath = documentTemplate.getAtPath();

        if (!(dataModel instanceof XDocReportModel)) {
            throw new IllegalArgumentException("Data model must be an instance of XDocReportModel (was instead: " + dataModel.getClass().getName() + ")");
        }

        final XDocReportModel xDocReportModel = (XDocReportModel) dataModel;

        return xDocReportService.render(typeRef, atPath, xDocReportModel, outputType);
    }

    @Inject
    private DocumentRepository documentRepository;
    @Inject
    private ClockService clockService;
    @Inject
    private XDocReportService xDocReportService;

}
