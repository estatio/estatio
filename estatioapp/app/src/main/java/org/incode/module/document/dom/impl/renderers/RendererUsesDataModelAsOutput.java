package org.incode.module.document.dom.impl.renderers;

import java.io.IOException;

import org.incode.module.document.dom.impl.types.DocumentType;

/**
 * A trivial implementation of {@link RendererFromCharsToChars} that expects the dataModel to be a String,
 * and simply returns that as the output.
 */
public class RendererUsesDataModelAsOutput implements RendererFromCharsToChars {

    public String renderCharsToChars(
            final DocumentType documentType,
            final String variant, final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException {
        return (String) dataModel;
    }
}
