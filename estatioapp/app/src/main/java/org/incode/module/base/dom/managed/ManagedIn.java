package org.incode.module.base.dom.managed;

import org.incode.module.base.dom.TitledEnum;

/**
 * Enumerates the different systems that act as the source of truth, that is, are &quot;in the lead&quot;
 */
public enum ManagedIn implements TitledEnum {

    ESTATIO("Estatio"),
    CODA("Coda");

    private String title;

    ManagedIn(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}
