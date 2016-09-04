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

import org.apache.isis.applib.services.config.ConfigurationService;

import org.incode.module.documents.dom.rendering.RendererFromBytesToBytes;
import org.incode.module.documents.dom.rendering.RendererFromBytesToUrl;
import org.incode.module.documents.dom.types.DocumentType;

public class RendererForSsrs implements RendererFromBytesToBytes, RendererFromBytesToUrl {

    @Inject
    ConfigurationService configurationService;

    @Override public byte[] renderBytesToBytes(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final byte[] templateBytes,
            final Object dataModel,
            final String documentName) throws IOException {

        // TODO: call renderToUrl and then slurp the page down using OpenURLConnection or similar.
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public URL renderBytesToUrl(
            final DocumentType documentType,
            final String atPath,
            final long templateVersion,
            final byte[] templateBytes,
            final Object dataModel,
            final String documentName) throws IOException {

        // TODO: fetch the text out of the documentTemplate, interpolate using StringInterpolator, invoke the URL
        throw new RuntimeException("Not yet implemented");
    }
}
