package org.incode.module.base.dom;

public enum MimeTypeEnum {

    APPLICATION_PDF ("application/pdf"),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    TEXT_HTML("text/html");

    private final String str;

    MimeTypeEnum(final String s){
        str = s;
    }

    public String asStr() {
        return str;
    }

}
