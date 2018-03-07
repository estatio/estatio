package org.estatio.module.application.spiimpl.email;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailServiceForEstatio_isConfigured_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IsisConfiguration mockConfiguration;

    EmailServiceForEstatio esfe;

    @Before
    public void setUp() throws Exception {
        esfe = new EmailServiceForEstatio();
        esfe.configuration = mockConfiguration;
    }

    @Test
    public void when_hostname_is_null() throws Exception {

        // expect
        allowingConfigurationToReturn(null);

        // when
        final boolean configured = esfe.isConfigured();

        // then
        assertThat(configured).isFalse();
    }


    @Test
    public void when_hostname_is_empty() throws Exception {

        // expect
        allowingConfigurationToReturn("");

        // when
        final boolean configured = esfe.isConfigured();

        // then
        assertThat(configured).isFalse();
    }

    @Test
    public void when_hostname_is_not_empty() throws Exception {

        // expect
        allowingConfigurationToReturn("some.smtp.server");

        // when
        final boolean configured = esfe.isConfigured();

        // then
        assertThat(configured).isTrue();
    }

    private void allowingConfigurationToReturn(final String result) {
        context.checking(new Expectations() {{
            allowing(mockConfiguration).getString("isis.service.email.sender.hostname");
            will(returnValue(result));
        }});
    }


}