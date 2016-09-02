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

import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.rendering.RendererToBytes;
import org.incode.module.documents.dom.rendering.RendererToUrl;

public class Ssrs implements RendererToBytes, RendererToUrl {

    @Override
    public byte[] renderToBytes(
            final DocumentTemplate documentTemplate, final Object dataModel) throws IOException {

        // TODO: call renderToUrl and then slurp the page down using OpenURLConnection or similar.

        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public URL renderToUrl(
            final DocumentTemplate documentTemplate, final Object dataModel, final String documentName)
            throws IOException {

        // TODO: fetch the text out of the documentTemplate, interpolate using StringInterpolator, invoke the URL

        // TOFIX: I've just realized that my assumption that the DocumentNature of both the DocumentTemplate and
        // the resultant Document generated from it, would be the same is wrong... so need to slacken off those rules
        // and change the implementation of DocumentTemplate#render(...) to look at the nature of the associated
        // RenderingStrategy ... if it's a character, then store as clob, else store as blob.
        //

        throw new RuntimeException("Not yet implemented");
    }
}
