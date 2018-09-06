package org.incode.module.document.dom.impl.docs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.activation.DataSource;

import org.apache.commons.io.output.NullOutputStream;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

public enum DocumentSort {
    /**
     * Stored as a BLOB
     */
    EMPTY(DocumentNature.BYTES, DocumentStorage.INTERNAL) {
        @Override
        public DataSource asDataSource(DocumentAbstract<?> document) {
            throw new IllegalStateException("Document not yet rendered");
        }
    },
    /**
     * Stored as a BLOB
     */
    BLOB(DocumentNature.BYTES, DocumentStorage.INTERNAL) {
        @Override
        public DataSource asDataSource(final DocumentAbstract<?> document) {
            return new ByteDataSource(document, asBytes(document));
        }

        @Override
        public byte[] asBytes(final DocumentAbstract<?> document) {
            return document.getBlobBytes();
        }
    },
    /**
     * Stored as a CLOB
     */
    CLOB(DocumentNature.CHARACTERS, DocumentStorage.INTERNAL) {
        @Override
        public DataSource asDataSource(final DocumentAbstract<?> document) {
            return new CharDataSource(document, asChars(document));
        }

        @Override
        public String asChars(final DocumentAbstract<?> document) {
            return document.getClobChars();
        }
    },
    /**
     * Stored in-situ (as a LONGVARCHAR)
     */
    TEXT(DocumentNature.CHARACTERS, DocumentStorage.INTERNAL) {
        @Override
        public DataSource asDataSource(final DocumentAbstract<?> document) {
            return new CharDataSource(document, asChars(document));
        }

        @Override
        public String asChars(final DocumentAbstract<?> document) {
            return document.getText();
        }
    },
    /**
     * Stored externally as blob (holds a hyperlink to access)
     *
     * Only {@link Document}s can be stored externally (not {@link DocumentTemplate}).
     */
    EXTERNAL_BLOB(DocumentNature.BYTES, DocumentStorage.EXTERNAL) {
        @Override
        public DataSource asDataSource(final DocumentAbstract<?> document) {
            return new ByteDataSource(document, asBytes(document));
        }

        @Override
        public byte[] asBytes(final DocumentAbstract<?> document) {
            final FactoryService factoryService = document.factoryService;
            final Blob blob = factoryService.mixin(Document_downloadExternalUrlAsBlob.class, document).$$();
            return blob != null ? blob.getBytes() : null;
        }
    },
    /**
     * Stored externally as clob (holds a hyperlink to access).
     * 
     * Only {@link Document}s can be stored externally (not {@link DocumentTemplate}).
     */
    EXTERNAL_CLOB(DocumentNature.BYTES, DocumentStorage.EXTERNAL) {
        @Override
        public DataSource asDataSource(final DocumentAbstract<?> document) {
            return new CharDataSource(document, asChars(document));
        }

        @Override
        public String asChars(final DocumentAbstract<?> document) {
            final FactoryService factoryService = document.factoryService;
            final Clob clob = factoryService.mixin(Document_downloadExternalUrlAsClob.class, document).$$();
            final CharSequence chars = clob.getChars();
            return chars instanceof String ? (String) chars : chars.toString();
        }
    };

    private final DocumentNature nature;
    private final DocumentStorage storage;

    private DocumentSort(DocumentNature nature, DocumentStorage storage) {
        this.nature = nature;
        this.storage = storage;
    }

    @Programmatic
    public abstract DataSource asDataSource(final DocumentAbstract<?> document);

    @Programmatic
    public String asChars(final DocumentAbstract<?> document) {
        throw new IllegalArgumentException("Cannot convert to characters");
    }

    /**
     * Supported only if {@link #isBytes()}.
     */
    @Programmatic
    public byte[] asBytes(final DocumentAbstract<?> document) {
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

    private static abstract class DocumentDataSource implements DataSource {

        final DocumentAbstract<?> document;
        public DocumentDataSource(final DocumentAbstract<?> document) {
            this.document = document;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return new NullOutputStream();
        }

        @Override
        public String getContentType() {
            return document.getMimeType();
        }

        @Override
        public String getName() {
            return document.getName();
        }
    }

    private static class ByteDataSource extends DocumentDataSource {

        private static final Charset CHARSET = StandardCharsets.UTF_8;

        private final byte[] bytes;

        public ByteDataSource(final DocumentAbstract<?> document, final byte[] bytes) {
            super(document);
            this.bytes = bytes;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(bytes);
        }
    }

    private static class CharDataSource extends DocumentDataSource {

        private static final Charset CHARSET = StandardCharsets.UTF_8;

        private final String chars;

        public CharDataSource(final DocumentAbstract<?> document, final String chars) {
            super(document);
            this.chars = chars;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(chars.getBytes(CHARSET));
        }
    }


}
