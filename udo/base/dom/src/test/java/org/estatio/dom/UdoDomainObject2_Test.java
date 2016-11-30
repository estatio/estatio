package org.estatio.dom;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class UdoDomainObject2_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class SomeDomainObject extends UdoDomainObject2<SomeDomainObject> {

        private String applicationTenancyPath = "/";

        public String getApplicationTenancyPath() {
            return applicationTenancyPath;
        }

        public void setApplicationTenancyPath(final String applicationTenancyPath) {
            this.applicationTenancyPath = applicationTenancyPath;
        }

        public ApplicationTenancy getApplicationTenancy() {
            return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
        }

        public SomeDomainObject(final String keyProperties) {
            super(keyProperties);
        }
    }

    ;

    @Mock
    ApplicationTenancyRepository mockApplicationTenancies;

    SomeDomainObject domainObject;

    @Before
    public void setUp() throws Exception {
        domainObject = new SomeDomainObject("name");
        domainObject.securityApplicationTenancyRepository = mockApplicationTenancies;
    }

    public static class GetApplicationTenancy extends UdoDomainObject2_Test {

        @Test
        public void whenNonNull() throws Exception {

            // given
            final ApplicationTenancy result = new ApplicationTenancy();
            domainObject.setApplicationTenancyPath("/a/b/c");

            // then
            context.checking(new Expectations() {{
                oneOf(mockApplicationTenancies).findByPathCached("/a/b/c");
                will(returnValue(result));
            }});

            // when
            final ApplicationTenancy applicationTenancy = domainObject.getApplicationTenancy();

            // and then
            assertThat(applicationTenancy).isEqualTo(result);
        }

        @Test
        public void whenNull() throws Exception {

            // given
            domainObject.setApplicationTenancyPath(null);

            // then
            context.checking(new Expectations() {{
                oneOf(mockApplicationTenancies).findByPathCached(null);
                will(returnValue(null));
            }});

            // when
            final ApplicationTenancy applicationTenancy = domainObject.getApplicationTenancy();

            // and then
            assertThat(applicationTenancy).isNull();
        }

    }
}