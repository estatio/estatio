package org.estatio.dom;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class EstatioDomainObjectTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class SomeDomainObject extends EstatioDomainObject<SomeDomainObject> {

        private String applicationTenancyPath = "/";

        public String getApplicationTenancyPath() {
            return applicationTenancyPath;
        }

        public void setApplicationTenancyPath(final String applicationTenancyPath) {
            this.applicationTenancyPath = applicationTenancyPath;
        }

        public ApplicationTenancy getApplicationTenancy() {
            return applicationTenancies.findTenancyByPath(getApplicationTenancyPath());
        }

        public SomeDomainObject(final String keyProperties) {
            super(keyProperties);
        }
    };

    @Mock
    ApplicationTenancies mockApplicationTenancies;

    SomeDomainObject domainObject;

    @Before
    public void setUp() throws Exception {
        domainObject = new SomeDomainObject("name");
        domainObject.applicationTenancies = mockApplicationTenancies;
    }

    public static class GetApplicationTenancy extends EstatioDomainObjectTest {

        @Test
        public void whenNonNull() throws Exception {

            // given
            final ApplicationTenancy result = new ApplicationTenancy();
            domainObject.setApplicationTenancyPath("/a/b/c");

            // then
            context.checking(new Expectations() {{
                oneOf(mockApplicationTenancies).findTenancyByPath("/a/b/c");
                will(returnValue(result));
            }});

            // when
            final ApplicationTenancy applicationTenancy = domainObject.getApplicationTenancy();

            // and then
            assertThat(applicationTenancy, is(result));
        }

        @Test
        public void whenNull() throws Exception {

            // given
            domainObject.setApplicationTenancyPath(null);

            // then
            context.checking(new Expectations() {{
                oneOf(mockApplicationTenancies).findTenancyByPath(null);
                will(returnValue(null));
            }});

            // when
            final ApplicationTenancy applicationTenancy = domainObject.getApplicationTenancy();

            // and then
            assertThat(applicationTenancy, is(nullValue()));
        }

    }
}