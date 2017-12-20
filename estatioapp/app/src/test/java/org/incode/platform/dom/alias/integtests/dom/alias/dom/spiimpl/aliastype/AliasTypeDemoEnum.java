package org.incode.platform.dom.alias.integtests.dom.alias.dom.spiimpl.aliastype;

import org.apache.isis.applib.annotation.Title;

import org.incode.module.alias.dom.spi.AliasType;

import lombok.Getter;

public enum AliasTypeDemoEnum implements AliasType {

    // in UK and NL
    GENERAL_LEDGER("GL"),
    // in UK and NL
    DOCUMENT_MANAGEMENT("DOC"),
    // in UK only
    PERSONNEL_SYSTEM("HR")
    ;

    @Title
    @Getter
    private final String id;

    AliasTypeDemoEnum(final String id) {
        this.id = id;
    }

}

