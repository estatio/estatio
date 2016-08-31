package org.isisaddons.module.freemarker.dom.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.isisaddons.module.freemarker.dom.spi.FreeMarkerTemplateLoader;
import org.isisaddons.module.freemarker.dom.spi.TemplateSource;

class TemplateLoaderDelegatingToInjectedLoaders implements freemarker.cache.TemplateLoader {

    private final List<FreeMarkerTemplateLoader> freemarkerFreeMarkerTemplateLoaders;

    public TemplateLoaderDelegatingToInjectedLoaders(final List<FreeMarkerTemplateLoader> freemarkerFreeMarkerTemplateLoaders) {
        if (freemarkerFreeMarkerTemplateLoaders == null || freemarkerFreeMarkerTemplateLoaders.isEmpty()) {
            throw new IllegalStateException("No freemarker template loaders available");
        }
        this.freemarkerFreeMarkerTemplateLoaders = freemarkerFreeMarkerTemplateLoaders;
    }

    @Override
    public Object findTemplateSource(final String templateName) throws IOException {
        final String[] split = FreeMarkerService.split(templateName);
        final String templateReference = split[0];
        final String templateAtPath = split[1];
        for (FreeMarkerTemplateLoader freemarkerFreeMarkerTemplateLoader : freemarkerFreeMarkerTemplateLoaders) {
            final TemplateSource templateSource = freemarkerFreeMarkerTemplateLoader
                    .load(templateReference, templateAtPath);
            if (templateSource != null) {
                return templateSource;
            }
        }
        return null;
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
