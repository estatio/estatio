package org.estatio.module.numerator.dom;

import java.math.BigInteger;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorRepository_create_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    NumeratorRepository numeratorRepository;

    @Mock
    BookmarkService mockBookmarkService;

    @Mock
    RepositoryService mockRepositoryService;

    Country stubCountry = new Country();

    ApplicationTenancy stubApplicationTenancy = new ApplicationTenancy() {{
        setPath("/ITA");
    }};

    static final String NAME = "ABC";
    static final String FORMAT = "ABC-%04d";

    Object object1;
    Object object2;

    @Before
    public void setUp() throws Exception {
        numeratorRepository = new NumeratorRepository() {
            @Override protected <T> T persist(final T transientDomainObject) {
                return transientDomainObject;
            }
        };

        numeratorRepository.repositoryService = mockRepositoryService;
        numeratorRepository.bookmarkService = mockBookmarkService;
        object1 = new Object();
        object2 = new Object();
    }

    @Test
    public void when_null_country() throws Exception {

        Numerator numeratorTest = new Numerator("ABC", null, stubApplicationTenancy.getPath(), FORMAT, BigInteger.ZERO);

        // expecting
        context.checking(new Expectations() {{
            never(mockBookmarkService);
            oneOf(mockRepositoryService).persistAndFlush(with(any(Numerator.class)));
            will(returnValue(numeratorTest));
        }});

        // when
        Numerator numerator = numeratorRepository
                .create(NAME, null, null, null, FORMAT, BigInteger.ZERO, stubApplicationTenancy);

        // then
        assertThat(numerator.getName()).isEqualTo(NAME);
        assertThat(numerator.getCountry()).isNull();
        assertThat(numerator.getFormat()).isEqualTo(FORMAT);
        assertThat(numerator.getApplicationTenancyPath()).isSameAs(stubApplicationTenancy.getPath());
    }

    @Test
    public void when_null_objects() throws Exception {

        Numerator numeratorTest = new Numerator("ABC", stubCountry, stubApplicationTenancy.getPath(), FORMAT, BigInteger.ZERO);

        // expecting
        context.checking(new Expectations() {{
            never(mockBookmarkService);
            oneOf(mockRepositoryService).persistAndFlush(with(any(Numerator.class)));
            will(returnValue(numeratorTest));
        }});

        // when
        Numerator numerator = numeratorRepository
                .create(NAME, stubCountry, null, null, FORMAT, BigInteger.ZERO, stubApplicationTenancy);

        // then
        assertThat(numerator.getCountry()).isSameAs(stubCountry);
        assertThat(numerator.getObjectType()).isNull();
        assertThat(numerator.getObjectIdentifier()).isNull();
        assertThat(numerator.getObjectType2()).isNull();
        assertThat(numerator.getObjectIdentifier2()).isNull();

    }

    @Test
    public void when_non_null_object1() throws Exception {

        Numerator numeratorTest = new Numerator("ABC", stubCountry, stubApplicationTenancy.getPath(), FORMAT, BigInteger.ZERO);
        numeratorTest.setObjectType("PROP");
        numeratorTest.setObjectIdentifier("123");

        // expecting
        context.checking(new Expectations() {{
            allowing(mockBookmarkService).bookmarkFor(object1);
            will(returnValue(new Bookmark("PROP", "123")));
            oneOf(mockRepositoryService).persistAndFlush(with(any(Numerator.class)));
            will(returnValue(numeratorTest));
        }});


        // when
        Numerator numerator = numeratorRepository
                .create("ABC", stubCountry, object1, null, FORMAT, BigInteger.ZERO, stubApplicationTenancy);

        // then
        assertThat(numerator.getObjectType()).isEqualTo("PROP");
        assertThat(numerator.getObjectIdentifier()).isEqualTo("123");
        assertThat(numerator.getObjectType2()).isNull();
        assertThat(numerator.getObjectIdentifier2()).isNull();

    }
    @Test
    public void when_non_null_object1_and_non_null_object2() throws Exception {

        Numerator numeratorTest = new Numerator("ABC", stubCountry, stubApplicationTenancy.getPath(), FORMAT, BigInteger.ZERO);
        numeratorTest.setObjectType("PROP");
        numeratorTest.setObjectIdentifier("123");
        numeratorTest.setObjectType2("ORG");
        numeratorTest.setObjectIdentifier2("456");

        // expecting
        context.checking(new Expectations() {{
            allowing(mockBookmarkService).bookmarkFor(object1);
            will(returnValue(new Bookmark("PROP", "123")));

            allowing(mockBookmarkService).bookmarkFor(object2);
            will(returnValue(new Bookmark("ORG", "456")));

            oneOf(mockRepositoryService).persistAndFlush(with(any(Numerator.class)));
            will(returnValue(numeratorTest));
        }});

        // when
        Numerator numerator = numeratorRepository
                .create("ABC", stubCountry, object1, object2, FORMAT, BigInteger.ZERO, stubApplicationTenancy);

        // then
        assertThat(numerator.getObjectType()).isEqualTo("PROP");
        assertThat(numerator.getObjectIdentifier()).isEqualTo("123");
        assertThat(numerator.getObjectType2()).isEqualTo("ORG");
        assertThat(numerator.getObjectIdentifier2()).isEqualTo("456");

    }
}