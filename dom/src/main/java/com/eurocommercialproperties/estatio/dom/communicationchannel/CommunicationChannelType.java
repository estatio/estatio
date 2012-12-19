/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eurocommercialproperties.estatio.dom.communicationchannel;

import com.eurocommercialproperties.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

public enum CommunicationChannelType {
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
            CommunicationChannel contactMechanism = container.newTransientInstance(cls);
            contactMechanism.setType(this);
            return contactMechanism;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
    
    public String title() {
        return StringUtils.enumTitle(this.toString());
    }

}
