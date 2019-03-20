package org.incode.module.base.dom;

public enum MimeTypeData {

    APPLICATION_PDF (Str.APPLICATION_PDF),
    APPLICATION_DOCX(Str.APPLICATION_DOCX),
    TEXT_HTML (Str.TEXT_HTML);

    public final String str;

    MimeTypeData(final String s){
        str = s;
    }

    public String asStr() {
        return str;
    }

    public boolean matches(HasMimeType hasMimeType) {
        return str.equals(hasMimeType.getMimeType());
    }

    public static class Str {
        public static final String APPLICATION_PDF = "application/pdf";
        public static final String APPLICATION_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String TEXT_HTML = "text/html";
    }

}
