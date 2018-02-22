package org.incode.module.slack.impl;

import org.apache.log4j.BasicConfigurator;

import org.apache.isis.core.commons.config.IsisConfigurationDefault;

/**
 * compile using:

   mvn clean install -Dmain

 * run using:

   java -Disis.service.errorReporting.slack.authToken=xxx \
        -Disis.service.errorReporting.slack.channel=ecp-estatio-error-tst \
        -Dhttp.proxyHost=10.1.0.4 \
        -Dhttp.proxyPort=3128 \
        -jar target/estatio-slack-1.27.0-SNAPSHOT-shaded.jar \
        "Hi there !!!"

 */
public class SlackMain {

    public static void main(String[] args) {

        BasicConfigurator.configure();

        final IsisConfigurationDefault configuration = new IsisConfigurationDefault();
        putConfig(configuration, "authToken");
        putConfig(configuration, "channel");

        final SlackService slackService = new SlackService();
        slackService.configuration = configuration;

        slackService.init();

        final String message = args.length > 0 ? args[0] : "Hello, world";

        slackService.sendMessage(message);

        slackService.destroy();
    }

    private static void putConfig(final IsisConfigurationDefault configuration, final String suffix) {
        final String key = ErrorReportingServiceForSlack.CONFIG_KEY_PREFIX + suffix;
        final String value = System.getProperty(key);
        if(value == null) {
            throw new RuntimeException("Missing system property\n\n-D" + key + "=\n");
        }
        configuration.put(key, value);
    }

}
