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
package org.estatio.module.application.spiimpl.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.base.Strings;

import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.core.commons.config.IsisConfiguration;

import org.isisaddons.module.security.app.user.MeService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.IPartyRoleType;

@DomainService(menuOrder = "99")
public class EmailServiceForEstatio implements EmailService {

    private static final String ISIS_SERVICE_EMAIL_OVERRIDE_TO = "isis.service.email.override.to";
    private static final String ISIS_SERVICE_EMAIL_OVERRIDE_CC = "isis.service.email.override.cc";
    private static final String ISIS_SERVICE_EMAIL_OVERRIDE_BCC = "isis.service.email.override.bcc";

    private String overrideTo;
    private String overrideCc;
    private String overrideBcc;

    @PostConstruct
    @Programmatic
    public void init() {
        overrideTo = configuration.getString(ISIS_SERVICE_EMAIL_OVERRIDE_TO);
        overrideCc = configuration.getString(ISIS_SERVICE_EMAIL_OVERRIDE_CC);
        overrideBcc = configuration.getString(ISIS_SERVICE_EMAIL_OVERRIDE_BCC);
    }

    @Override
    public boolean send(final List<String> to, final List<String> cc, final List<String> bcc, final String subject, final String body, final DataSource... attachments) {
        final List<String> actualTo = actually(to, overrideTo);
        final List<String> actualCc = actually(cc, overrideCc);
        final List<String> actualBcc = actually(bcc, overrideBcc);
        return delegate.send(actualTo, actualCc, actualBcc, subject, body, attachments);
    }

    private static List<String> actually(final List<String> original, final String overrideIfAny) {
        return Strings.isNullOrEmpty(overrideIfAny) ? original : Collections.singletonList(overrideIfAny);
    }

    @Override
    public boolean isConfigured() {
        return !Strings.isNullOrEmpty(configuration.getString("isis.service.email.sender.hostname"));
    }

    public boolean sendToCurrentUser(final String subject, final String body){
        // try to get email address from user as Person
        final Person userAsPerson = personRepository.findByUsername(meService.me().getUsername());
        String emailAddress = null;
        if (userAsPerson!=null){
            final EmailAddress address = (EmailAddress) communicationChannelRepository.findByOwnerAndType(userAsPerson, CommunicationChannelType.EMAIL_ADDRESS).first();
            if (address!=null) {
                emailAddress = address.getEmailAddress();
            }
        }
        if (emailAddress==null){
            // fallback to ApplicationUser#getEmailAddreess()
            emailAddress = meService.me().getEmailAddress();
        }
        if (emailAddress!=null){
            return this.send(Arrays.asList(emailAddress), Lists.emptyList(), Lists.emptyList(),
                    subject,
                    body);
        }
        return false;
    }

    public boolean sendToUsersWithRoleTypeAndAtPath(final IPartyRoleType roleType, final String atPath, final String subject, final String body){
        final List<Person> personsForRoleType = personRepository.findByRoleTypeAndAtPath(roleType, atPath);
        final List<String> to = new ArrayList<>();
        for (Person person : personsForRoleType){
            final EmailAddress address = (EmailAddress) communicationChannelRepository.findByOwnerAndType(person, CommunicationChannelType.EMAIL_ADDRESS).first();
            if (address!=null) {
                to.add(address.getEmailAddress());
            }
        }
        if (!to.isEmpty()) {
            return send(to, Collections.emptyList(), Collections.emptyList(), subject, body);
        } else {
            return false;
        }
    }

    @Inject
    EmailServiceThrowingException delegate;

    @javax.inject.Inject
    IsisConfiguration configuration;

    @Inject PersonRepository personRepository;

    @Inject MeService meService;

    @Inject CommunicationChannelRepository communicationChannelRepository;

}