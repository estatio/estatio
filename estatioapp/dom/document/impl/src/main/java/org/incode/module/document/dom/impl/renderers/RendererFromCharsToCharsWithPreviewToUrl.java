package org.incode.module.document.dom.impl.renderers;

import java.io.IOException;
import java.net.URL;

import org.incode.module.document.dom.impl.types.DocumentType;

public interface RendererFromCharsToCharsWithPreviewToUrl extends RendererFromCharsToChars, PreviewToUrl {

    URL previewCharsToChars(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException;

}
