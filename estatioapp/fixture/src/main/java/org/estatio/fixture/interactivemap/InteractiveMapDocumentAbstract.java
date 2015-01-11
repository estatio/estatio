/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

package org.estatio.fixture.interactivemap;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;

import org.apache.wicket.util.io.IOUtils;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.apache.isis.applib.value.Blob;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.document.InteractiveMapDocuments;

public abstract class InteractiveMapDocumentAbstract extends DiscoverableFixtureScript {

    protected void createDocument(String fileName, FixedAsset fixedAsset) {
        documents.newDocument(fileName, resourceAsBlob(fileName), fixedAsset);
    }

    private Blob resourceAsBlob(String fileName) {
        try {
            InputStream is = getClass().getResourceAsStream("/svg/" + fileName);
            final String mimeType = new MimetypesFileTypeMap().getContentType(fileName);
            Blob blob = new Blob(fileName, mimeType, IOUtils.toByteArray(is));
            is.close();
            return blob;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private InteractiveMapDocuments documents;
}
