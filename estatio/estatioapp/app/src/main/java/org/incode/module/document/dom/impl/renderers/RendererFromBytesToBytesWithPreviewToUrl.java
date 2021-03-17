package org.incode.module.document.dom.impl.renderers;

import java.io.IOException;
import java.net.URL;

import org.incode.module.document.dom.impl.types.DocumentType;

public interface RendererFromBytesToBytesWithPreviewToUrl extends RendererFromBytesToBytes, PreviewToUrl {

    URL previewBytesToBytes(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final byte[] templateBytes,
            final Object dataModel) throws IOException;

}
