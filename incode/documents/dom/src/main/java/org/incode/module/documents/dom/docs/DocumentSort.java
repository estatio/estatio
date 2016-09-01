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
package org.incode.module.documents.dom.docs;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.documents.dom.templates.DocumentTemplate;

public enum DocumentSort {
    /**
     * Stored as a BLOB
     */
    BLOB {
        @Override
        public byte[] asBytes(final DocumentTemplate documentTemplate) {
            return documentTemplate.getBlobBytes();
        }
    },
    /**
     * Stored as a CLOB
     */
    CLOB {
        @Override
        public String asChars(final DocumentTemplate documentTemplate) {
            return documentTemplate.getClobChars();
        }
    },
    /**
     * Stored in-situ (as a LONGVARCHAR)
     */
    TEXT {
        @Override
        public String asChars(final DocumentTemplate documentTemplate) {
            return documentTemplate.getText();
        }
    },
    /**
     * Stored externally as blob (holds a hyperlink to access)
     */
    EXTERNAL_BLOB {
        @Override
        public byte[] asBytes(final DocumentTemplate documentTemplate) {
            throw new IllegalStateException("Not yet implemented");
        }
    },
    /**
     * Stored externally as clob (holds a hyperlink to access)
     */
    EXTERNAL_CLOB {
        @Override
        public String asChars(final DocumentTemplate documentTemplate) {
            throw new IllegalStateException("Not yet implemented");
        }
    };

    /**
     * Supported only if {@link #isClob()} or {@link #isText()}.
     */
    @Programmatic
    public String asChars(final DocumentTemplate documentTemplate) {
        throw new IllegalArgumentException("Cannot convert to characters");
    }

    /**
     * Supported only if {@link #isBinary()}.
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
    public boolean isBinary() {
        return this == BLOB || this == EXTERNAL_BLOB;
    }

    @Programmatic
    public boolean isClob() {
        return this == CLOB || this == EXTERNAL_CLOB;
    }

    @Programmatic
    public boolean isText() {
        return this == TEXT;
    }
}
