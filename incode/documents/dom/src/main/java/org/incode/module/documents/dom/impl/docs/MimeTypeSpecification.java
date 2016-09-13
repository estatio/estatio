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
package org.incode.module.documents.dom.impl.docs;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.spec.AbstractSpecification2;

public class MimeTypeSpecification extends AbstractSpecification2<String> {
    @Override
    public TranslatableString satisfiesTranslatableSafely(final String mimeType) {
        try {
            new MimeType(mimeType);
        } catch (MimeTypeParseException e) {
            return TranslatableString.tr("Invalid mime type");
        }
        return null;
    }
}
