package org.incode.module.docrendering.stringinterpolator.dom.impl;

import java.io.IOException;
import java.net.URL;

import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytesWithPreviewToUrl;
import org.incode.module.document.dom.impl.types.DocumentType;

public class RendererForStringInterpolatorPreviewAndCaptureUrl extends RendererForStringInterpolatorCaptureUrl
        implements RendererFromCharsToBytesWithPreviewToUrl {

    @Override
    public URL previewCharsToBytes(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException {
        return super.previewCharsToBytes(
                documentType, atPath, templateVersion, templateChars, dataModel);
    }


}
