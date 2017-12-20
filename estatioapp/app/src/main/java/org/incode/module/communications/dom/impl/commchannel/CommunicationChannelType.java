package org.incode.module.communications.dom.impl.commchannel;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

public enum CommunicationChannelType implements TitledEnum {

    POSTAL_ADDRESS(PostalAddress.class), 
    EMAIL_ADDRESS(EmailAddress.class), 
    PHONE_NUMBER(PhoneOrFaxNumber.class), 
    FAX_NUMBER(PhoneOrFaxNumber.class);

    private Class<? extends CommunicationChannel> cls;

    private CommunicationChannelType(final Class<? extends CommunicationChannel> cls) {
        this.cls = cls;
    }

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }
    
    public static List<CommunicationChannelType> matching(final Class<? extends CommunicationChannel> cls) {
        return Lists.newArrayList(Iterables.filter(Arrays.asList(values()), new Predicate<CommunicationChannelType>(){

            @Override
            public boolean apply(final CommunicationChannelType input) {
                return input.cls == cls;
            }}));
    }


    // //////////////////////////////////////

    public static class Type {
        private Type(){}

        public final static int MAX_LEN = 30;
    }

}
