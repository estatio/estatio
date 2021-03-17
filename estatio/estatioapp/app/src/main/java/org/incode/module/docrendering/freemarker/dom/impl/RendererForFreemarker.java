package org.incode.module.docrendering.freemarker.dom.impl;

import java.io.IOException;

import javax.inject.Inject;

import com.google.common.base.Joiner;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.module.document.dom.impl.renderers.RendererFromCharsToChars;
import org.incode.module.document.dom.impl.types.DocumentType;

import freemarker.template.TemplateException;

public class RendererForFreemarker implements RendererFromCharsToChars {

    public String renderCharsToChars(
            final DocumentType documentType,
            final String variant,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException {

        try {
            final String templateName = join(documentType.getReference(), variant, atPath, "" + templateVersion);

            return freeMarkerService.render(templateName, templateChars, dataModel);

        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    private static String join(final String... parts) {
        return Joiner.on(":").join(parts);
    }

    @Inject
    FreeMarkerService freeMarkerService;

}
