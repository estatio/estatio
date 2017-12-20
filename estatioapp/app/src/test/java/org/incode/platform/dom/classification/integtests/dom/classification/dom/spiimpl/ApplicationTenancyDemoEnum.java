package org.incode.platform.dom.classification.integtests.dom.classification.dom.spiimpl;

/**
 * In a real (not demo) application this would probably be the isisaddons' security module's <code>ApplicationTenancy</code> entity.
 */
public enum ApplicationTenancyDemoEnum {

    GLOBAL("/"),
    UK("/uk"),
    NL("/nl"),
    IT("/it"),
    SW("/sw"),
    FR("/fr");

    private final String path;

    ApplicationTenancyDemoEnum(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

