/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.docrendering.stringinterpolator.dom;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.services.config.ConfigurationService;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.docrendering.stringinterpolator.dom.spi.UrlDownloaderService;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytes;
import org.incode.module.document.dom.impl.types.DocumentType;

public class RendererUsingStringInterpolatorCaptureUrl implements RendererFromCharsToBytes {

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
