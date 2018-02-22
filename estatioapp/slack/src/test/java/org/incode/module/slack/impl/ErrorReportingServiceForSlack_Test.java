package org.incode.module.slack.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.error.ErrorDetails;
import org.apache.isis.applib.services.error.Ticket;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assume.assumeTrue;

/**
 * run using:

 -Disis.service.errorReporting.slack.authToken=XXXXXX
 -Disis.service.errorReporting.slack.channel=ecp-estatio-error-tst

 */
public class ErrorReportingServiceForSlack_Test {

    ErrorReportingServiceForSlack errorReportingService;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IsisConfiguration mockIsisConfiguration;

    @Before
    public void setUp() throws Exception {
        errorReportingService = new ErrorReportingServiceForSlack();
        errorReportingService.configuration = mockIsisConfiguration;
        errorReportingService.slackService = new SlackService();

        context.checking(new Expectations() {{
            allowingGetStringReturnSystemProperty("authToken");
            allowingGetStringReturnSystemProperty("channel");
            allowing(mockIsisConfiguration);
        }

        private void allowingGetStringReturnSystemProperty(final String suffix) {
                allowing(mockIsisConfiguration).getString("isis.service.errorReporting.slack." + suffix);
                will(returnValue(System.getProperty("isis.service.errorReporting.slack." + suffix)));
            }
        });

        errorReportingService.init();
        final boolean initialized = errorReportingService.isInitialized();

        assumeTrue(initialized);
    }

    public static class reportError extends ErrorReportingServiceForSlack_Test {

        @Test
        public void happy_case() throws Exception {

            final String message = "Some recognised message";
            final List<String> lines = asExceptionStackTraceLines();

            final ErrorDetails errorDetails = new ErrorDetails(message, true, false, lines, Collections.singletonList(lines));
            final Ticket ticket = errorReportingService.reportError(errorDetails);


            Assert.assertThat(ticket, is(not(nullValue())));
        }

        private static List<String> asExceptionStackTraceLines() {
            final Exception dummy = new Exception("underlying exception reason");
            dummy.fillInStackTrace();

            return asStackTrace(dummy)
                    .stream()
                    .map(StackTraceDetail::getLine)
                    .collect(Collectors.toList());
        }

        private static List<StackTraceDetail> asStackTrace(Throwable ex) {
            List<StackTraceDetail> stackTrace = Lists.newArrayList();
            List<Throwable> causalChain = Throwables.getCausalChain(ex);
            boolean firstTime = true;
            for(Throwable cause: causalChain) {
                if(!firstTime) {
                    stackTrace.add(StackTraceDetail.spacer());
                    stackTrace.add(StackTraceDetail.causedBy());
                    stackTrace.add(StackTraceDetail.spacer());
                } else {
                    firstTime = false;
                }
                append(cause, stackTrace);
            }
            return stackTrace;
        }


        private static void append(final Throwable cause, final List<StackTraceDetail> stackTrace) {
            stackTrace.add(StackTraceDetail.exceptionClassName(cause));
            stackTrace.add(StackTraceDetail.exceptionMessage(cause));
            for (StackTraceElement el : cause.getStackTrace()) {
                stackTrace.add(StackTraceDetail.element(el));
            }
        }

    }


    static class StackTraceDetail implements Serializable {

        private static final long serialVersionUID = 1L;

        private static StackTraceDetail exceptionClassName(Throwable cause) {
            return new StackTraceDetail(
                    cause.getClass().getName());
        }

        private static StackTraceDetail exceptionMessage(Throwable cause) {
            return new StackTraceDetail(
                    cause.getMessage());
        }

        private  static StackTraceDetail element(StackTraceElement el) {
            StringBuilder buf = new StringBuilder();
            buf .append("    ")
                    .append(el.getClassName())
                    .append("#")
                    .append(el.getMethodName())
                    .append("(")
                    .append(el.getFileName())
                    .append(":")
                    .append(el.getLineNumber())
                    .append(")\n")
            ;
            return new StackTraceDetail(
                    buf.toString());
        }

        private  static StackTraceDetail spacer() {
            return new StackTraceDetail(
                    "");
        }

        private  static StackTraceDetail causedBy() {
            return new StackTraceDetail(
                    "Caused by:");
        }

        enum Type {
            EXCEPTION_CLASS_NAME,
            EXCEPTION_MESSAGE,
            STACKTRACE_ELEMENT,
            LITERAL
        }

        private final String line;

        private  StackTraceDetail(String line) {
            this.line = line;
        }
        private  String getLine() {
            return line;
        }

    }

}

