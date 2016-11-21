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
package org.estatio.app.services.email;

import java.util.Collections;
import java.util.List;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.base.Strings;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.core.commons.config.IsisConfiguration;

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

    @Override public boolean send(final List<String> to, final List<String> cc, final List<String> bcc, final String subject, final String body, final DataSource... attachments) {
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
        return "proxy.ecp.loc".equals(configuration.getString("isis.service.email.sender.hostname")) || delegate.isConfigured();
    }

    @Inject
    EmailServiceThrowingException delegate;

    @javax.inject.Inject
    IsisConfiguration configuration;

}