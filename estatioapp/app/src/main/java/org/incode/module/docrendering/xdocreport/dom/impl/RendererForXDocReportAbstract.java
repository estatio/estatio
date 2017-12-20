package org.incode.module.docrendering.xdocreport.dom.impl;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.xdocreport.dom.service.OutputType;
import org.isisaddons.module.xdocreport.dom.service.XDocReportModel;
import org.isisaddons.module.xdocreport.dom.service.XDocReportService;

import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToBytes;
import org.incode.module.document.dom.impl.types.DocumentType;

public abstract class RendererForXDocReportAbstract implements RendererFromBytesToBytes {

    private final OutputType outputType;

    protected RendererForXDocReportAbstract(final OutputType outputType) {
        this.outputType = outputType;
    }

    @Override
    public byte[] renderBytesToBytes(
            final DocumentType documentType, final String variant, final String atPath, final long version,
            final byte[] templateBytes, final Object dataModel)
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
