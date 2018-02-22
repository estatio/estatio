package org.incode.module.slack.impl;

import java.io.IOException;
import java.net.Proxy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.common.base.Strings;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackPreparedMessage;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.replies.SlackMessageReply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.commons.config.IsisConfiguration;

import lombok.Setter;
import static org.incode.module.slack.impl.ErrorReportingServiceForSlack.CONFIG_KEY_PREFIX;

@DomainService(nature = NatureOfService.DOMAIN)
public class SlackService {

    private static final Logger LOG = LoggerFactory.getLogger(SlackService.class);

    /** Mandatory ... either set or specify as configuration property. */
    @Setter
    private String authToken;

    /** Mandatory ... either set or specify as configuration property. */
    @Setter
    private String channel;

    private boolean configured;

    /**
     * Populated only if {@link #isConfigured()}.
     */
    private SlackSession slackSession;
    private SlackChannel slackChannel;

    @PostConstruct
    @Programmatic
    public void init() {
        this.authToken = asSetElseConfigured(this.authToken, "authToken");
        this.channel = asSetElseConfigured(this.channel, "channel");

        boolean requiredConfiguration =
                !Strings.isNullOrEmpty(authToken) &&
                        !Strings.isNullOrEmpty(channel);

        if(!requiredConfiguration) {
            return;
        }

        final SlackSessionFactory.SlackSessionFactoryBuilder builder =
                SlackSessionFactory.getSlackSessionBuilder(authToken);
        final String proxyHost = System.getProperty("http.proxyHost");
        if(proxyHost != null) {
            final Integer proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
            builder.withProxy(Proxy.Type.HTTP,proxyHost, proxyPort).build();
        }

        final SlackSession slackSession = builder.build();
        try {
            slackSession.connect();
        } catch (IOException e) {
            LOG.warn("Failed to connect to Slack", e);
            return;
        }
        final SlackChannel slackChannel = slackSession.findChannelByName(channel);
        if(slackChannel == null) {
            disconnect(slackSession);
            return;
        }

        this.slackSession = slackSession;
        this.slackChannel = slackChannel;

        this.configured = true;
    }

    @Programmatic
    public boolean isConfigured() {
        return configured;
    }

    @Programmatic
    public String getChannel() {
        return channel;
    }

    @Programmatic
    public void sendMessage(final String message) {

        final SlackPreparedMessage preparedMessage =
                new SlackPreparedMessage.Builder()
                        .withMessage(message)
                        .withUnfurl(false)
                        .withLinkNames(true)
                        .build();

        sendMessage(preparedMessage);
    }

    public SlackMessageHandle<SlackMessageReply> sendMessage(final SlackPreparedMessage preparedMessage) {
        return slackSession.sendMessage(slackChannel, preparedMessage);
    }


    @PreDestroy
    public void destroy() {
        if(!isConfigured()) {
            return;
        }
        disconnect(slackSession);
    }

    private void disconnect(final SlackSession slackSession) {
        if(slackSession == null) {
            return;
        }
        try {
            slackSession.disconnect();
        } catch (IOException e) {
            LOG.warn("Failed to disconnect, ignoring");
        }
    }

    private String asSetElseConfigured(final String field, final String configKeySuffix) {
        return coalesce(field, configValue(configKeySuffix));
    }

    private String configValue(final String configKeySuffix) {
        return configuration.getString(CONFIG_KEY_PREFIX + configKeySuffix);
    }

    private static String coalesce(String... values) {
        for (String value : values) {
            if (!Strings.isNullOrEmpty(value)) {
                return value;
            }

        }
        return null;
    }

    @javax.inject.Inject
    IsisConfiguration configuration;

}
