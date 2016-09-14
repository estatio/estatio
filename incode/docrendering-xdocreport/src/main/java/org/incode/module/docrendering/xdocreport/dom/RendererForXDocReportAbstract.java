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
package org.incode.module.docrendering.xdocreport.dom;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.xdocreport.dom.service.OutputType;
import org.isisaddons.module.xdocreport.dom.service.XDocReportModel;
import org.isisaddons.module.xdocreport.dom.service.XDocReportService;

import org.incode.module.documents.dom.impl.docs.DocumentRepository;
import org.incode.module.documents.dom.impl.rendering.RendererFromBytesToBytes;
import org.incode.module.documents.dom.impl.types.DocumentType;

public abstract class RendererForXDocReportAbstract implements RendererFromBytesToBytes {

    private final OutputType outputType;

    protected RendererForXDocReportAbstract(final OutputType outputType) {
        this.outputType = outputType;
    }

    @Override
    public byte[] renderBytesToBytes(
            final DocumentType documentType, final String atPath, final long version,
            final byte[] templateBytes, final Object dataModel, final String documentName)
            throws IOException {

        if (!(dataModel instanceof XDocReportModel)) {
            throw new IllegalArgumentException("Data model must be an instance of XDocReportModel (was instead: " + dataModel.getClass().getName() + ")");
        }

        final XDocReportModel xDocReportModel = (XDocReportModel) dataModel;

        return xDocReportService.render(templateBytes, xDocReportModel, outputType);
    }

    @Inject
    private DocumentRepository documentRepository;
    @Inject
    private ClockService clockService;
    @Inject
    private XDocReportService xDocReportService;

}
