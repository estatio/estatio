package org.incode.module.base.dom;

import org.apache.isis.applib.value.Blob;

import lombok.Getter;

public enum MimeTypeData {

    APPLICATION_PDF (Str.APPLICATION_PDF, ".pdf"),
    APPLICATION_DOCX(Str.APPLICATION_DOCX, ".docx"),
    TEXT_HTML (Str.TEXT_HTML, ".html");

    private final String str;
    @Getter
    private final String fileSuffix;

    MimeTypeData(final String str, final String fileSuffix){
        this.str = str;
        this.fileSuffix = fileSuffix;
    }

    public String asStr() {
        return str;
    }

    public boolean matches(HasMimeType hasMimeType) {
        return str.equals(hasMimeType.getMimeType());
    }

    public Blob newBlob(final String name, final byte[] bytes) {
        final String pdfName = name.endsWith(fileSuffix) ? name : name + fileSuffix;
        return new Blob(pdfName, this.asStr(), bytes);
    }

    public static class Str {
        public static final String APPLICATION_PDF = "application/pdf";
        public static final String APPLICATION_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String TEXT_HTML = "text/html";
    }

}
