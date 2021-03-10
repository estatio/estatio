package org.incode.module.communications.dom.impl.commchannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.managed.HasManagedIn;
import org.incode.module.base.dom.managed.ManagedIn;

import lombok.Getter;

public enum CommunicationChannelPurposeType implements TitledEnum, HasManagedIn {

    ACCOUNTING ("Accounting", ManagedIn.ESTATIO),
    INVOICING  ("Invoicing",  ManagedIn.ESTATIO),
    SUPPLIER   ("Supplier",   ManagedIn.CODA);

    private String title;
    @Getter
    private final ManagedIn managedIn;

    CommunicationChannelPurposeType(
            String title,
            final ManagedIn managedIn) {
        this.title = title;
        this.managedIn = managedIn;
    }

    public static List<CommunicationChannelPurposeType> managedIn(ManagedIn managedIn) {
        return Collections.unmodifiableList(
                Arrays.stream(values())
                        .filter(x -> x.managedIn == managedIn)
                        .collect(Collectors.toList()));
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
