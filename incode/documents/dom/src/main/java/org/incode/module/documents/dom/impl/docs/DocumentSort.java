/*
 *  Copyright 2016 Dan Haywood
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

import org.apache.isis.applib.annotation.Programmatic;

public enum DocumentSort {
    /**
     * Stored as a BLOB
     */
    BLOB(DocumentNature.BYTES, DocumentStorage.INTERNAL) {
        @Override
        public byte[] asBytes(final DocumentTemplate documentTemplate) {
            return documentTemplate.getBlobBytes();
        }
    },
    /**
     * Stored as a CLOB
     */
    CLOB(DocumentNature.CHARACTERS, DocumentStorage.INTERNAL) {
        @Override
        public String asChars(final DocumentTemplate documentTemplate) {
            return documentTemplate.getClobChars();
        }
    },
    /**
     * Stored in-situ (as a LONGVARCHAR)
     */
    TEXT(DocumentNature.CHARACTERS, DocumentStorage.INTERNAL) {
        @Override
        public String asChars(final DocumentTemplate documentTemplate) {
            return documentTemplate.getText();
        }
    },
    /**
     * Stored externally as blob (holds a hyperlink to access)
     *
     * Only {@link Document}s can be stored externally (not {@link DocumentTemplate}).
     */
    EXTERNAL_BLOB(DocumentNature.BYTES, DocumentStorage.EXTERNAL) {
        @Override
        public byte[] asBytes(final DocumentTemplate documentTemplate) {
            throw new IllegalStateException("Not yet implemented");
        }
    },
    /**
     * Stored externally as clob (holds a hyperlink to access).
     * 
     * Only {@link Document}s can be stored externally (not {@link DocumentTemplate}).
     */
    EXTERNAL_CLOB(DocumentNature.BYTES, DocumentStorage.EXTERNAL) {
        @Override
        public String asChars(final DocumentTemplate documentTemplate) {
            throw new IllegalStateException("Not yet implemented");
        }
    };

    private final DocumentNature nature;
    private final DocumentStorage storage;

    private DocumentSort(DocumentNature nature, DocumentStorage storage) {
        this.nature = nature;
        this.storage = storage;
    }

    @Programmatic
    public String asChars(final DocumentTemplate documentTemplate) {
        throw new IllegalArgumentException("Cannot convert to characters");
    }

    /**
     * Supported only if {@link #isBytes()}.
     */
    @Programmatic
    public byte[] asBytes(final DocumentTemplate documentTemplate) {
        throw new IllegalArgumentException("Cannot convert to bytes");
    }

    @Programmatic
    public DocumentSort asExternal() {
        if(this == BLOB) { return EXTERNAL_BLOB; }
        if(this == CLOB || this == TEXT) { return EXTERNAL_CLOB; }
        return this;
    }

    @Programmatic
    public boolean isBytes() {
        return nature == DocumentNature.BYTES;
    }

    @Programmatic
    public boolean isCharacters() {
        return nature == DocumentNature.CHARACTERS;
    }

    @Programmatic
    public boolean isExternal() {
        return storage == DocumentStorage.EXTERNAL;
    }
}
