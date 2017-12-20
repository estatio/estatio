package org.incode.module.docrendering.stringinterpolator.dom.impl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.services.config.ConfigurationService;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.docrendering.stringinterpolator.dom.spi.UrlDownloaderService;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytes;
import org.incode.module.document.dom.impl.types.DocumentType;

public class RendererForStringInterpolatorCaptureUrl implements RendererFromCharsToBytes {

    @Override
    public byte[] renderCharsToBytes(
            final DocumentType documentType,
            final String variant, final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException {
        final URL url =
                previewCharsToBytes(documentType, atPath, templateVersion, templateChars, dataModel);

        for (UrlDownloaderService downloaderService : downloaderServices) {
            if(downloaderService.canDownload(url)) {
                return downloaderService.download(url);
            }
        }
        throw new IllegalStateException("No downloader service available to download from " + url);
    }

    protected URL previewCharsToBytes(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException {

        final StringInterpolatorService.Root root = (StringInterpolatorService.Root) dataModel;
        final String urlStr = stringInterpolator.interpolate(root, templateChars);
        return new URL(urlStr);
    }

    @Inject
    List<UrlDownloaderService> downloaderServices;

    @Inject
    StringInterpolatorService stringInterpolator;

    @Inject
    ConfigurationService configurationService;

}
