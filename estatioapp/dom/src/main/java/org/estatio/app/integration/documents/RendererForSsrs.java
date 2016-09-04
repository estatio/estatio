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
package org.estatio.app.integration.documents;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import org.apache.isis.applib.services.config.ConfigurationService;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.documents.dom.rendering.RendererFromCharsToBytesWithPreviewToUrl;
import org.incode.module.documents.dom.types.DocumentType;

public class RendererForSsrs implements RendererFromCharsToBytesWithPreviewToUrl {

    @Override
    public byte[] renderCharsToBytes(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel,
            final String documentName) throws IOException {
        final URL url =
                previewCharsToBytes(documentType, atPath, templateVersion, templateChars, dataModel, documentName);
        final ByteSource byteSource = Resources.asByteSource(url);
        return byteSource.read();
    }

    @Override
    public URL previewCharsToBytes(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel,
            final String documentName) throws IOException {

        final StringInterpolatorService.Root root = (StringInterpolatorService.Root) dataModel;
        final String urlStr = stringInterpolator.interpolate(root, templateChars);
        return new URL(urlStr);
    }

    @javax.inject.Inject
    StringInterpolatorService stringInterpolator;

    @Inject
    ConfigurationService configurationService;

}
