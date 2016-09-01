package org.isisaddons.module.freemarker.dom.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.isisaddons.module.freemarker.dom.spi.FreeMarkerTemplateLoader;
import org.isisaddons.module.freemarker.dom.spi.TemplateSource;

class TemplateLoaderDelegatingToInjectedLoaders implements freemarker.cache.TemplateLoader {

    private final List<FreeMarkerTemplateLoader> freeMarkerTemplateLoaders;

    public TemplateLoaderDelegatingToInjectedLoaders(final List<FreeMarkerTemplateLoader> freeMarkerTemplateLoaders) {
        if (freeMarkerTemplateLoaders == null || freeMarkerTemplateLoaders.isEmpty()) {
            throw new IllegalStateException("No freemarker template loaders available");
        }
        this.freeMarkerTemplateLoaders = freeMarkerTemplateLoaders;
    }

    @Override
    public Object findTemplateSource(final String templateName) throws IOException {
        final String[] split = FreeMarkerService.split(templateName);
        final String documentTypeRef = split[0];
        final String atPath = split[1];
        for (FreeMarkerTemplateLoader freemarkerFreeMarkerTemplateLoader : freeMarkerTemplateLoaders) {
            final TemplateSource templateSource = freemarkerFreeMarkerTemplateLoader
                    .templateSourceFor(documentTypeRef, atPath);
            if (templateSource != null) {
                return templateSource;
            }
        }
        throw new IllegalStateException(
                String.format("Unable to find template for type '%s', atPath '%s'", documentTypeRef, atPath));
    }

    @Override
    public Reader getReader(final Object templateSourceAsObj, final String encoding) throws IOException {
        final TemplateSource templateSource = (TemplateSource) templateSourceAsObj;
        return new StringReader(templateSource.getChars());
    }

    @Override
    public void closeTemplateSource(final Object o) throws IOException {
        // no-op
    }

    @Override
    public long getLastModified(final Object templateSourceAsObj) {
        final TemplateSource templateSource = (TemplateSource) templateSourceAsObj;
        return templateSource.getVersion();
    }
}
