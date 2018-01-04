package org.incode.module.docrendering.stringinterpolator.dom.impl;

import java.io.IOException;

import javax.inject.Inject;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.document.dom.impl.renderers.RendererFromCharsToChars;
import org.incode.module.document.dom.impl.types.DocumentType;

public class RendererForStringInterpolator implements RendererFromCharsToChars {

    @Override
    public String renderCharsToChars(
            final DocumentType documentType,
            final String variant,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException {
        final StringInterpolatorService.Root root = (StringInterpolatorService.Root) dataModel;
        return stringInterpolator.interpolate(root, templateChars);
    }

    @Inject
    StringInterpolatorService stringInterpolator;
}
