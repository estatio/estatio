package org.incode.module.document.dom.impl.renderers;

import java.io.IOException;

import org.incode.module.document.dom.impl.types.DocumentType;

public interface RendererFromCharsToChars extends Renderer {

    String renderCharsToChars(
            final DocumentType documentType,
            final String variant,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException;

}
