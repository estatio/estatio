package org.estatio.dom.communicationchannel;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.PowerType;
import org.estatio.dom.TitledEnum;
import org.estatio.dom.utils.StringUtils;

public enum CommunicationChannelType implements TitledEnum, PowerType<CommunicationChannel> {

    ACCOUNTING_POSTAL_ADDRESS(PostalAddress.class), 
    POSTAL_ADDRESS(PostalAddress.class), 
    ACCOUNTING_EMAIL_ADDRESS(EmailAddress.class), 
    EMAIL_ADDRESS(EmailAddress.class), 
    PHONE_NUMBER(PhoneNumber.class), 
    FAX_NUMBER(FaxNumber.class);

    private Class<? extends CommunicationChannel> cls;

    private CommunicationChannelType(Class<? extends CommunicationChannel> cls) {
        this.cls = cls;
    }

    public CommunicationChannel create(DomainObjectContainer container) {
        try {
            CommunicationChannel cc = container.newTransientInstance(cls);
            cc.setType(this);
            return cc;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }

}
