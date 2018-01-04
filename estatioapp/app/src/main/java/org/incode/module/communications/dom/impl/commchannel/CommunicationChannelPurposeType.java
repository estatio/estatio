package org.incode.module.communications.dom.impl.commchannel;

import org.incode.module.base.dom.TitledEnum;

public enum CommunicationChannelPurposeType implements TitledEnum {

    ACCOUNTING("Accounting"),
    INVOICING("Invoicing");

    private String title;

    private CommunicationChannelPurposeType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }


    // //////////////////////////////////////

    public static class Meta {
        private Meta(){}

        public final static int MAX_LEN = 30;
    }

}
