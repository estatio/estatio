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

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;

import com.google.common.base.Strings;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceClassPathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.runtime.services.email.EmailServiceDefault;

// a copy of EmailServiceDefault, with a couple of tweaks
@DomainService(
        menuOrder = "110"
)
public class EmailServiceThrowingException {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceDefault.class);

    //region > constants
    private static final String ISIS_SERVICE_EMAIL_SENDER_ADDRESS = "isis.service.email.sender.address";
    private static final String ISIS_SERVICE_EMAIL_SENDER_PASSWORD = "isis.service.email.sender.password";

    private static final String ISIS_SERVICE_EMAIL_SENDER_HOSTNAME = "isis.service.email.sender.hostname";
    private static final String ISIS_SERVICE_EMAIL_SENDER_HOSTNAME_DEFAULT = "smtp.gmail.com";

    private static final String ISIS_SERVICE_EMAIL_PORT = "isis.service.email.port";
    private static final int ISIS_SERVICE_EMAIL_PORT_DEFAULT = 587;

    private static final String ISIS_SERVICE_EMAIL_TLS_ENABLED = "isis.service.email.tls.enabled";
    private static final boolean ISIS_SERVICE_EMAIL_TLS_ENABLED_DEFAULT = true;

    private static final String ISIS_SOCKET_TIMEOUT = "isis.service.email.socket.timeout";
    private static final int ISIS_SOCKET_TIMEOUT_DEFAULT = 5000;

    private static final String ISIS_SOCKET_CONNECTION_TIMEOUT = "isis.service.email.socket.connection.timeout";
    private static final int ISIS_SOCKET_CONNECTION_TIMEOUT_DEFAULT = 5000;

    private static final String ISIS_BACKOFF_INTERVAL = "isis.service.email.backoff.interval";
    private static final int ISIS_BACKOFF_INTERVAL_DEFAULT = 5000;

    private static final String ISIS_ATTEMPTS = "isis.service.email.attempts";
    private static final int ISIS_ATTEMPTS_DEFAULT = 3;

    //endregion

    //region > init
    private boolean initialized;

    /**
     * Loads responsive email templates borrowed from http://zurb.com/ink/templates.php (Basic)
     */
    @PostConstruct
    @Programmatic
    public void init() {

        if (initialized) {
            return;
        }

        initialized = true;

        if (!isConfigured()) {
            LOG.warn("NOT configured");
        } else {
            LOG.debug("configured");
        }
    }

    protected String getSenderEmailAddress() {
        return configuration.getString(ISIS_SERVICE_EMAIL_SENDER_ADDRESS);
    }

    protected String getSenderEmailPassword() {
        return configuration.getString(ISIS_SERVICE_EMAIL_SENDER_PASSWORD);
    }

    protected String getSenderEmailHostName() {
        return configuration.getString(ISIS_SERVICE_EMAIL_SENDER_HOSTNAME, ISIS_SERVICE_EMAIL_SENDER_HOSTNAME_DEFAULT);
    }

    protected Integer getSenderEmailPort() {
        return configuration.getInteger(ISIS_SERVICE_EMAIL_PORT, ISIS_SERVICE_EMAIL_PORT_DEFAULT);
    }

    protected Boolean getSenderEmailTlsEnabled() {
        return configuration.getBoolean(ISIS_SERVICE_EMAIL_TLS_ENABLED, ISIS_SERVICE_EMAIL_TLS_ENABLED_DEFAULT);
    }

    protected int getSocketTimeout() {
        return configuration.getInteger(ISIS_SOCKET_TIMEOUT, ISIS_SOCKET_TIMEOUT_DEFAULT);
    }

    protected int getSocketConnectionTimeout() {
        return configuration.getInteger(ISIS_SOCKET_CONNECTION_TIMEOUT, ISIS_SOCKET_CONNECTION_TIMEOUT_DEFAULT);
    }

    protected int getBackoffInterval() {
        return configuration.getInteger(ISIS_BACKOFF_INTERVAL, ISIS_BACKOFF_INTERVAL_DEFAULT);
    }

    protected int getAttempts() {
        return configuration.getInteger(ISIS_ATTEMPTS, ISIS_ATTEMPTS_DEFAULT);
    }
    //endregion

    //region > isConfigured

    @Programmatic
    public boolean isConfigured() {
        return !Strings.isNullOrEmpty(getSenderEmailAddress()) && !Strings.isNullOrEmpty(getSenderEmailPassword());
    }
    //endregion

    //region > send
    @Programmatic
    public boolean send(
            final List<String> toList, final List<String> ccList, final List<String> bccList,
            final String subject, final String body, final DataSource... attachments) {
        return send(toList, ccList, bccList, getSenderEmailAddress(), subject, body, attachments);
    }

    @Programmatic
    public boolean send(
            final List<String> toList, final List<String> ccList, final List<String> bccList,
            final String from,
            final String subject, final String body, final DataSource... attachments) {

        final int[] backoffIntervals = buildBackoffIntervals(getAttempts(), getBackoffInterval());
        for (final int backoffInterval : backoffIntervals) {
            if (buildAndSendMessage(
                    toList, ccList, bccList, from, subject, body, attachments,
                    backoffInterval, backoffIntervals.length)) {
                break;
            }
        }

        return true;
    }

    private boolean buildAndSendMessage(
            final List<String> toList,
            final List<String> ccList,
            final List<String> bccList,
            final String from,
            final String subject,
            final String body,
            final DataSource[] attachments,
            final int backoffIntervalMs, final int numBackoffIntervals) {

        final ImageHtmlEmail email = buildMessage(
                toList, ccList, bccList, from, subject, body, attachments);
        return sendMessage(email, backoffIntervalMs, numBackoffIntervals);
    }

    private ImageHtmlEmail buildMessage(
            final List<String> toList,
            final List<String> ccList,
            final List<String> bccList,
            final String from,
            final String subject,
            final String body,
            final DataSource[] attachments) {

        final ImageHtmlEmail email;
        try {
            String passwordToUse = getSenderEmailPassword();

            if (!from.equals(getSenderEmailAddress())) {
                final String emailKey = configuration.asMap()
                        .entrySet()
                        .stream()
                        .filter(entry -> from.equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
                        .get(0);
                passwordToUse = configuration.getString(emailKey.replaceFirst("address", "password"));
            }

            email = new ImageHtmlEmail();

            email.setAuthenticator(new DefaultAuthenticator(from, passwordToUse));
            email.setHostName(getSenderEmailHostName());
            email.setSmtpPort(getSenderEmailPort());
            email.setStartTLSEnabled(getSenderEmailTlsEnabled());
            email.setDataSourceResolver(new DataSourceClassPathResolver("/", true));

            email.setSocketTimeout(getSocketTimeout());
            email.setSocketConnectionTimeout(getSocketConnectionTimeout());

            final Properties properties = email.getMailSession().getProperties();

            properties.put("mail.debug", "true");

            email.setFrom(from);
            email.setSubject(subject);
            email.setHtmlMsg(body);
            email.setCharset("windows-1252");

            if (attachments != null && attachments.length > 0) {
                for (DataSource attachment : attachments) {
                    email.attach(attachment, attachment.getName(), "");
                }
            }

            if (notEmpty(toList)) {
                email.addTo(toList.toArray(new String[0]));
            }
            if (notEmpty(ccList)) {
                email.addCc(ccList.toArray(new String[0]));
            }
            if (notEmpty(bccList)) {
                email.addBcc(bccList.toArray(new String[0]));
            }

        } catch (EmailException ex) {

            LOG.error("An error occurred while trying to prepare an email to be sent", ex);

            throw new RuntimeException(ex);
        }
        return email;
    }

    private boolean sendMessage(
            final ImageHtmlEmail email, final int backoffIntervalMs, final int numBackoffIntervals) {
        try {
            email.send();
            return true;
        } catch (EmailException ex) {

            if(backoffIntervalMs == 0) {
                LOG.error(String.format(
                            "Failed to send an email after %d attempts; giving up", numBackoffIntervals),
                          ex);
                throw new RuntimeException(ex);
            }

            LOG.warn(String.format(
                        "Failed to send an email; sleeping for %d seconds then will retry", backoffIntervalMs),
                     ex);
            sleep(backoffIntervalMs);
            return false;
        }
    }

    static int[] buildBackoffIntervals(final int attempts, final int backoffInterval) {
        int[] backoffIntervals = new int[attempts];
        for (int i = 0; i < backoffIntervals.length; i++) {
            backoffIntervals[i] = backoffInterval;
            if(i == backoffIntervals.length - 1) {
                backoffIntervals[i] = 0;
            }
        }
        return backoffIntervals;
    }

    private static void sleep(final int backoffIntervalMs) {
        try {
            Thread.sleep(backoffIntervalMs);
        } catch (InterruptedException e1) {
            // ignore
        }
    }
    //endregion

    //region > helper methods
    private boolean notEmpty(final List<String> toList) {
        return toList != null && !toList.isEmpty();
    }
    //endregion

    //endregion

    @javax.inject.Inject
    IsisConfiguration configuration;

}