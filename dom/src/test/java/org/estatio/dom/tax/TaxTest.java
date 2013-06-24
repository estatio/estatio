package org.estatio.dom.tax;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class TaxTest {

    @Mock
    TaxRates mockTaxRates;

    @Mock
    private DomainObjectContainer mockContainer;

    private Tax tax;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        
        context.checking(new Expectations() {
            {
                allowing(mockContainer).newTransientInstance(with(IsisMatchers.classEqualTo(TaxRate.class)));
                will(returnNewTransientInstance(TaxRate.class));
            }

            private <T> Action returnNewTransientInstance(final Class<T> cls) {
                return new Action(){

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("new transient instance of " + cls.getName());
                    }

                    @Override
                    public Object invoke(Invocation inv) throws Throwable {
                        Class<?> cls = (Class<?>) inv.getParameter(0);
                        Object obj = cls.newInstance();
                        final Method[] methods = cls.getMethods();
                        for (final Method method : methods) {
                            if (!method.getName().startsWith("setContainer")) {
                                continue;
                            }
                            if (method.getParameterTypes().length != 1) {
                                continue;
                            }
                            final Class<?> methodParameterType = method.getParameterTypes()[0];
                            if (methodParameterType.isAssignableFrom(DomainObjectContainer.class)) {
                                method.invoke(obj, mockContainer);
                                break;
                            }
                        }
                        return obj;
                    }};
            }
        });

        final BigDecimal p1;
        final BigDecimal p2;
        
        p1 = BigDecimal.valueOf(19);
        p2 = BigDecimal.valueOf(21);

        final LocalDate d1;
        final LocalDate d2;

        d1 = new LocalDate(1980, 1, 1);
        d2 = new LocalDate(2000, 1, 1);
        
        final TaxRate r1;
        final TaxRate r2;

        r1 = tax.newRate(d1, p1);
        r2 = r1.newRate(d2, p1);

        context.checking(new Expectations() {
            {
                oneOf(mockTaxRates).findTaxRateByTaxAndDate(with(equal(tax)), with(equal(d1)));
                will(returnValue(r1));
                oneOf(mockTaxRates).findTaxRateByTaxAndDate(with(equal(tax)), with(equal(d2)));
                will(returnValue(r2));
            }
        });

        tax.newRate(d1, p1).newRate(d2, p2);
        
    }

    @Ignore
    @Test
    public void testTaxPercentageForDate() {
        final LocalDate d1;
        d1 = new LocalDate(1980, 1, 1);
        
        assertEquals(BigDecimal.valueOf(19), tax.percentageFor(d1));
    }
}
