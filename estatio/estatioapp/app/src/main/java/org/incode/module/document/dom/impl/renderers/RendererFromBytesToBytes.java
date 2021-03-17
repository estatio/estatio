package org.incode.module.document.dom.impl.renderers;

import java.io.IOException;

import org.incode.module.document.dom.impl.types.DocumentType;

public interface RendererFromBytesToBytes extends Renderer {

    byte[] renderBytesToBytes(
            final DocumentType documentType,
            final String variant,
            final String atPath,
            final long templateVersion,
            final byte[] templateBytes,
            final Object dataModel)
            throws IOException;

}
