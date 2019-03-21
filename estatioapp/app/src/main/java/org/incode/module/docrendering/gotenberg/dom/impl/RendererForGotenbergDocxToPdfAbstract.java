package org.incode.module.docrendering.gotenberg.dom.impl;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.renderers.RendererFromBytesToBytes;
import org.incode.module.document.dom.impl.types.DocumentType;

public abstract class RendererForGotenbergDocxToPdfAbstract implements RendererFromBytesToBytes {

    /**
     * Expected to return a <code>.docx</code>.
     */
    private final RendererFromBytesToBytes renderBytesToBytes;

    protected RendererForGotenbergDocxToPdfAbstract(final RendererFromBytesToBytes renderBytesToBytes) {
        this.renderBytesToBytes = renderBytesToBytes;
    }

    @Override
    public byte[] renderBytesToBytes(
            final DocumentType documentType,
            final String variant,
            final String atPath,
            final long templateVersion,
            final byte[] templateBytes,
            final Object dataModel) throws IOException {
        final byte[] docx = renderBytesToBytes.renderBytesToBytes(documentType, variant, atPath, templateVersion, templateBytes, dataModel);
        return gotenbergClientService.convertToPdf(docx);
    }

    @Inject
    GotenbergClientService gotenbergClientService;

    @Inject
    public void setServiceRegistry2(final ServiceRegistry2 serviceRegistry2) {
        this.serviceRegistry2 = serviceRegistry2;
        this.serviceRegistry2.injectServicesInto(this.renderBytesToBytes);
    }
    ServiceRegistry2 serviceRegistry2;

}
