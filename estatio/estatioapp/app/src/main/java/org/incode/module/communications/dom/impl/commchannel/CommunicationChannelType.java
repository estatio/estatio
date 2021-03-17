package org.incode.module.communications.dom.impl.commchannel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

public enum CommunicationChannelType implements TitledEnum {

    POSTAL_ADDRESS(PostalAddress.class), 
    EMAIL_ADDRESS(EmailAddress.class), 
    PHONE_NUMBER(PhoneOrFaxNumber.class), 
    FAX_NUMBER(PhoneOrFaxNumber.class);

    private Class<? extends CommunicationChannel> implementationClass;

    private CommunicationChannelType(final Class<? extends CommunicationChannel> implementationClass) {
        this.implementationClass = implementationClass;
    }

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }
    
    public static List<CommunicationChannelType> matching(final Class<? extends CommunicationChannel> cls) {
        return Lists.newArrayList(
                Arrays.stream(values())
                        .filter(input -> input.implementationClass == cls)
                        .collect(Collectors.toList())
                );
    }
    @Programmatic
    public void ensureCompatible(final Class<? extends CommunicationChannel> cls) {
        if(cls != this.implementationClass) {
            throw new IllegalArgumentException(
                    String.format("Class '%s' is not compatible with type of '%s'", cls.getSimpleName(), this.name()));
        }
    }


    // //////////////////////////////////////

    public static class Type {
        private Type(){}

        public final static int MAX_LEN = 30;
    }

}
