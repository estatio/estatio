package org.estatio.module.base.platform.integtestsupport;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import static org.slf4j.event.Level.INFO;

public class LogConfig {
    private final String loggingPropertyFile;
    private final Level testLoggingLevel;
    private final PrintStream fixtureTracing;

    public LogConfig(
            final Level testLoggingLevel,
            final Level fixtureTracingLevel,
            final Logger fixtureTracingLogger) {
        this(testLoggingLevel, fixtureTracingLevel, fixtureTracingLogger, null);
    }
    public LogConfig(
            final Level testLoggingLevel,
            final Level fixtureTracingLevel,
            final Logger fixtureTracingLogger,
            final String loggingPropertyFile) {
        this(testLoggingLevel, LogStream.logPrintStream(fixtureTracingLogger, fixtureTracingLevel), loggingPropertyFile);
    }
    public LogConfig(
            final Level testLoggingLevel) {
        this(testLoggingLevel, (String)null);
    }
    public LogConfig(
            final Level testLoggingLevel,
            final String loggingPropertyFile) {
        this(testLoggingLevel, null, loggingPropertyFile);
    }
    public LogConfig(
            final Level testLoggingLevel,
            final PrintStream fixtureTracing) {
        this(testLoggingLevel, fixtureTracing, null);
    }
    public LogConfig(
            final Level testLoggingLevel,
            final PrintStream fixtureTracing,
            final String loggingPropertyFile) {
        this.testLoggingLevel = testLoggingLevel != null ? testLoggingLevel : INFO;
        this.fixtureTracing = fixtureTracing != null ? fixtureTracing : System.out;
        this.loggingPropertyFile =
                loggingPropertyFile != null ? loggingPropertyFile : "logging-integtest.properties";
    }

    public Level getTestLoggingLevel() {
        return testLoggingLevel;
    }

    public PrintStream getFixtureTracing() {
        return fixtureTracing;
    }

    public String getLoggingPropertyFile() {
        return loggingPropertyFile;
    }
}
