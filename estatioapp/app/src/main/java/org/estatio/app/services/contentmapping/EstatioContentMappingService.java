/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.app.services.contentmapping;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.google.common.base.Joiner;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.conmap.ContentMappingService;

import org.incode.module.communications.dom.impl.commchannel.PostalAddress;

import org.estatio.canonical.bankmandate.v1.BankAccountsAndMandatesDto;
import org.estatio.module.party.canonical.v1.PartyDtoFactory;
import org.estatio.canonical.party.v1.PartyDto;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.canonical.v1.BankMandateDtoFactory;
import org.estatio.module.bankmandate.canonical.v1.PartyBankAccountsAndMandatesDtoFactory;
import org.estatio.dom.communications.canonical.v1.PostalAddressDtoFactory;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.bankaccount.canonical.v1.BankAccountDtoFactory;
import org.estatio.module.lease.canonical.v1.InvoiceForLeaseDtoFactory;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioContentMappingService implements ContentMappingService {

    @Programmatic
    @Override
    public Object map(
            final Object object,
            final List<MediaType> acceptableMediaTypes) {

        final String domainType = determineDomainType(acceptableMediaTypes);

        if(object instanceof Party) {
            final Party party = (Party) object;
            if(domainType.equals(PartyDto.class.getName())) {
                return partyDtoFactory.newDto(party);
            }
            if(domainType.equals(BankAccountsAndMandatesDto.class.getName())) {
                return partyBankAccountsAndMandatesDtoFactory.newDto(party);
            }
        }

        if(object instanceof BankAccount) {
            return bankAccountDtoFactory.newDto((BankAccount)object);
        }
        if(object instanceof BankMandate) {
            return bankMandateDtoFactory.newDto((BankMandate)object);
        }
        if(object instanceof InvoiceForLease) {
            return invoiceForLeaseDtoFactory.newDto((InvoiceForLease)object);
        }
        if(object instanceof PostalAddress) {
            return postalAddressDtoFactory.newDto((PostalAddress)object);
        }

        return null;
    }

    static String determineDomainType(final List<MediaType> acceptableMediaTypes) {
        for (MediaType acceptableMediaType : acceptableMediaTypes) {
            final Map<String, String> parameters = acceptableMediaType.getParameters();
            final String domainType = parameters.get("x-ro-domain-type");
            if(domainType != null) {
                return domainType;
            }
        }
        throw new IllegalArgumentException(
                "Could not locate x-ro-domain-type parameter in any of the provided media types; got: " + Joiner.on(", ").join(acceptableMediaTypes));
    }

    @javax.inject.Inject
    BankAccountDtoFactory bankAccountDtoFactory;

    @javax.inject.Inject
    BankMandateDtoFactory bankMandateDtoFactory;

    @javax.inject.Inject
    PartyBankAccountsAndMandatesDtoFactory partyBankAccountsAndMandatesDtoFactory;

    @javax.inject.Inject
    PartyDtoFactory partyDtoFactory;

    @javax.inject.Inject
    InvoiceForLeaseDtoFactory invoiceForLeaseDtoFactory;

    @javax.inject.Inject
    PostalAddressDtoFactory postalAddressDtoFactory;

}
