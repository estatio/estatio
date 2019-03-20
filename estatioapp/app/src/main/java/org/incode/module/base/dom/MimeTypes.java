package org.incode.module.base.dom;

public enum MimeTypes {

    APPLICATION_PDF ("application/pdf"),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    TEXT_HTML("text/html");

    private final String str;

    MimeTypes(final String s){
        str = s;
    }

    public String asStr() {
        return str;
    }

}
