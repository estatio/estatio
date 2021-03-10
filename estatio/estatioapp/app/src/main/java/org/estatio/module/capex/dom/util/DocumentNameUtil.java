package org.estatio.module.capex.dom.util;

public class DocumentNameUtil {

    public static String stripPdfSuffixFromDocumentName(final String documentName){
        return documentName.replaceAll("(?i)\\.pdf", "");
    }

}
