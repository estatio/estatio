/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.communicationchannel;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.estatio.dom.TitledEnum;
import org.estatio.dom.utils.StringUtils;

public enum CommunicationChannelType implements TitledEnum {

    ACCOUNTING_POSTAL_ADDRESS(PostalAddress.class), 
    POSTAL_ADDRESS(PostalAddress.class), 
    ACCOUNTING_EMAIL_ADDRESS(EmailAddress.class), 
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

}
