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
package org.isisaddons.module.xdocreport.dom.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.config.ConfigurationService;

import org.isisaddons.module.xdocreport.dom.spi.XDocReportTemplateLoader;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.io.internal.ByteArrayOutputStream;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

@DomainService(nature = NatureOfService.DOMAIN)
public class XDocReportService {

    PdfOptions pdfOptions;

    @PostConstruct
    public void init() {
        if (xDocReportTemplateLoaders == null || xDocReportTemplateLoaders.isEmpty()) {
            throw new IllegalStateException("No template loaders available");
        }

        pdfOptions = PdfOptions.create();
    }

    /**
     * Uses the provided document type (reference) and atPath to lookup the template's text by way of any injected
     * {@link org.isisaddons.module.xdocreport.dom.spi.XDocReportTemplateLoader}s (acting as the glue between this
     * service and the consuming domain application).
     */
    @Programmatic
    public byte[] render(
            final String documentTypeReference,
            String atPath,
            final XDocReportModel dataModel,
            final OutputType outputType) throws IOException {
        final byte[] templateBytes = loadTemplate(documentTypeReference, atPath);
        try {
            final byte[] docxBytes = toDocx(templateBytes, dataModel);

            switch (outputType) {
            case PDF:
                return toPdf(docxBytes);
            default: // ie DOCX
                return docxBytes;
            }

        } catch (XDocReportException e) {
            throw new IOException(e);
        }
    }

    public byte[] toDocx(final byte[] bytes, final XDocReportModel dataModel) throws IOException, XDocReportException {
        IXDocReport report = XDocReportRegistry
                .getRegistry().loadReport(new ByteArrayInputStream(bytes), TemplateEngineKind.Freemarker);

        IContext context = report.createContext();

        FieldsMetadata fieldsMetadata = report.createFieldsMetadata();

        final Map<String, XDocReportModel.Data> contextObjects = dataModel.getContextData();
        for (Map.Entry<String, XDocReportModel.Data> entry : contextObjects.entrySet()) {
            final XDocReportModel.Data data = entry.getValue();
            final String key = entry.getKey();
            fieldsMetadata.load(key, data.getCls(), data.isList());
            context.put(key, data.getObj());
        }

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        report.process(context, baos);

        return baos.toByteArray();
    }

    private byte[] toPdf(final byte[] docxBytes) throws IOException {

        XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docxBytes));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfConverter.getInstance().convert(document, baos, pdfOptions);

        return baos.toByteArray();
    }

    private byte[] loadTemplate(final String documentTypeRef, final String atPath) {
        for (XDocReportTemplateLoader templateLoader : xDocReportTemplateLoaders) {
            final byte[] bytes = templateLoader.templateBytesFor(documentTypeRef, atPath);
            if(bytes != null) {
                return bytes;
            }
        }

        throw new IllegalStateException(
                String.format("Unable to find template for type '%s', atPath '%s'", documentTypeRef, atPath));
    }

    //region > injected services
    @Inject
    ConfigurationService configurationService;

    @Inject
    List<XDocReportTemplateLoader> xDocReportTemplateLoaders;
    //endregion

}



