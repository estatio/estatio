package org.estatio.dom.valuetypes;

import java.util.List;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class ApplicationTenancyLevelTest {

    static ApplicationTenancyLevel atl(String path) {
        return new ApplicationTenancyLevel(path);
    }

    public static class ParentOf_and_ChildOf extends ApplicationTenancyLevelTest {

        static void assertXxxOf(String target, String candidate, boolean expected) {
            Assert.assertThat(atl(target).parentOf(atl(candidate)), is(expected));
            Assert.assertThat(atl(candidate).childOf(atl(target)), is(expected));
        }

        @Test
        public void happyCase() throws Exception {
            assertXxxOf("/", "/a", true);
            assertXxxOf("/", "/a/bb", true);
            assertXxxOf("/", "/a/bb/ccc", true);
            assertXxxOf("/a", "/a/bb/ccc", true);
            assertXxxOf("/a/bb", "/a/bb/ccc", true);
        }

        @Test
        public void sadCase_whenChild() throws Exception {
            assertXxxOf("/a", "/", false);
            assertXxxOf("/a/bb", "/a", false);
            assertXxxOf("/a/bb/ccc", "/", false);
            assertXxxOf("/a/bb/ccc", "/a", false);
        }

        @Test
        public void sadCase_whenPeer() throws Exception {
            assertXxxOf("/a", "/b", false);
            assertXxxOf("/a/bb", "/a/zz", false);
            assertXxxOf("/a/bb/ccc", "/a/bb/yyy", false);
        }

        @Test
        public void sadCase_whenSameInstance() throws Exception {
            final ApplicationTenancyLevel ih = atl("/");
            final ApplicationTenancyLevel ih2 = atl("/a/bb");

            Assert.assertThat(ih.parentOf(ih), is(false));
            Assert.assertThat(ih2.parentOf(ih2), is(false));
        }

        @Test
        public void sadCase_whenEqual() throws Exception {
            assertXxxOf("/", "/", false);
            assertXxxOf("/a", "/a", false);
            assertXxxOf("/a/bb", "/a/bb", false);
        }

        @Test
        public void sadCase_parentOf_whenNull() throws Exception {
            Assert.assertThat(atl("/").parentOf(null), is(false));
        }

        @Test
        public void sadCase_childOf_whenNull() throws Exception {
            Assert.assertThat(atl("/").childOf(null), is(false));
        }
    }

    public static class GetParts extends ApplicationTenancyLevelTest {

        @Test
        public void happyCase() throws Exception {
            final List<String> parts = atl("/a/bb").getParts();

            Assert.assertThat(parts.size(), is(2));
            Assert.assertThat(parts.get(0), is("a"));
            Assert.assertThat(parts.get(1), is("bb"));
        }

        @Test
        public void root() throws Exception {
            final List<String> parts = atl("/").getParts();

            Assert.assertThat(parts.size(), is(0));
        }
    }

    public static class Parent extends ApplicationTenancyLevelTest {

        @Test
        public void happyCase() throws Exception {
            Assert.assertThat(atl("/a/bb").parent().toString(), is("/a"));
        }

        @Test
        public void root() throws Exception {
            Assert.assertThat(atl("/").parent(), is(nullValue()));
        }
    }

    public static class Child extends ApplicationTenancyLevelTest {

        @Test
        public void happyCase() throws Exception {
            Assert.assertThat(atl("/a").child("bb").toString(), is("/a/bb"));
        }

        @Test
        public void root() throws Exception {
            Assert.assertThat(atl("/").child("_"), is(atl("/_")));
        }

        @Test(expected = IllegalArgumentException.class)
        public void containsIllegalChars() throws Exception {
            atl("/").child("_/_");
        }
    }

    public static class IsXxx extends ApplicationTenancyLevelTest {

        @Test
        public void isRoot() throws Exception {
            Assert.assertThat(atl("/").isRoot(), is(true));
            Assert.assertThat(atl("/_").isRoot(), is(false));
            Assert.assertThat(atl("/it").isRoot(), is(false));
            Assert.assertThat(atl("/it/_").isRoot(), is(false));
            Assert.assertThat(atl("/it/car").isRoot(), is(false));
            Assert.assertThat(atl("/it/car/_").isRoot(), is(false));
            Assert.assertThat(atl("/it/car/ta").isRoot(), is(false));
        }
        @Test
        public void isRootOther() throws Exception {
            Assert.assertThat(atl("/").isRootOther(), is(false));
            Assert.assertThat(atl("/_").isRootOther(), is(true));
            Assert.assertThat(atl("/it").isRootOther(), is(false));
            Assert.assertThat(atl("/it/_").isRootOther(), is(false));
            Assert.assertThat(atl("/it/car").isRootOther(), is(false));
            Assert.assertThat(atl("/it/car/_").isRootOther(), is(false));
            Assert.assertThat(atl("/it/car/ta").isRootOther(), is(false));
        }
        @Test
        public void isCountry() throws Exception {
            Assert.assertThat(atl("/").isCountry(), is(false));
            Assert.assertThat(atl("/_").isCountry(), is(false));
            Assert.assertThat(atl("/it").isCountry(), is(true));
            Assert.assertThat(atl("/it/_").isCountry(), is(false));
            Assert.assertThat(atl("/it/car").isCountry(), is(false));
            Assert.assertThat(atl("/it/car/_").isCountry(), is(false));
            Assert.assertThat(atl("/it/car/ta").isCountry(), is(false));
        }
        @Test
        public void isCountryOnly() throws Exception {
            Assert.assertThat(atl("/").isCountryOther(), is(false));
            Assert.assertThat(atl("/_").isCountryOther(), is(false));
            Assert.assertThat(atl("/it").isCountryOther(), is(false));
            Assert.assertThat(atl("/it/_").isCountryOther(), is(true));
            Assert.assertThat(atl("/it/car").isCountryOther(), is(false));
            Assert.assertThat(atl("/it/car/_").isCountryOther(), is(false));
            Assert.assertThat(atl("/it/car/ta").isCountryOther(), is(false));
        }
        @Test
        public void isProperty() throws Exception {
            Assert.assertThat(atl("/").isProperty(), is(false));
            Assert.assertThat(atl("/_").isProperty(), is(false));
            Assert.assertThat(atl("/it").isProperty(), is(false));
            Assert.assertThat(atl("/it/_").isProperty(), is(false));
            Assert.assertThat(atl("/it/car").isProperty(), is(true));
            Assert.assertThat(atl("/it/car/_").isProperty(), is(false));
            Assert.assertThat(atl("/it/car/ta").isProperty(), is(false));
        }
        @Test
        public void isLocalDefault() throws Exception {
            Assert.assertThat(atl("/").isLocalDefault(), is(false));
            Assert.assertThat(atl("/_").isLocalDefault(), is(false));
            Assert.assertThat(atl("/it").isLocalDefault(), is(false));
            Assert.assertThat(atl("/it/_").isLocalDefault(), is(false));
            Assert.assertThat(atl("/it/car").isLocalDefault(), is(false));
            Assert.assertThat(atl("/it/car/_").isLocalDefault(), is(true));
            Assert.assertThat(atl("/it/car/ta").isLocalDefault(), is(false));
        }
        @Test
        public void isLocalNamed() throws Exception {
            Assert.assertThat(atl("/").isLocalNamed("ta"), is(false));
            Assert.assertThat(atl("/_").isLocalNamed("ta"), is(false));
            Assert.assertThat(atl("/it").isLocalNamed("ta"), is(false));
            Assert.assertThat(atl("/it/_").isLocalNamed("ta"), is(false));
            Assert.assertThat(atl("/it/car").isLocalNamed("ta"), is(false));
            Assert.assertThat(atl("/it/car/_").isLocalNamed("ta"), is(false));
            Assert.assertThat(atl("/it/car/ta").isLocalNamed("ta"), is(true));
        }
        @Test
        public void isPropertyOf() throws Exception {
            Assert.assertThat(atl("/").isPropertyOf(atl("/it")), is(false));
            Assert.assertThat(atl("/it").isPropertyOf(atl("/it")), is(false));
            Assert.assertThat(atl("/it/_").isPropertyOf(atl("/it")), is(false));
            Assert.assertThat(atl("/it/car").isPropertyOf(atl("/it")), is(true));
            Assert.assertThat(atl("/fr/car").isPropertyOf(atl("/it")), is(false));
            Assert.assertThat(atl("/it/car/_").isPropertyOf(atl("/it")), is(false));
            Assert.assertThat(atl("/it/car/ta").isPropertyOf(atl("/it")), is(false));
        }

    }

    public static class GetCountryPath extends ApplicationTenancyLevelTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenRoot() throws Exception {
            expectedException.expectMessage("root");
            atl("/").getCountryPath();
        }

        @Test
        public void whenRootOther() throws Exception {
            expectedException.expectMessage("root other");
            atl("/_").getCountryPath();
        }

        @Test
        public void whenNonRoot() throws Exception {
            Assert.assertThat(atl("/it").getCountryPath(), is("/it"));
            Assert.assertThat(atl("/it/_").getCountryPath(), is("/it"));
            Assert.assertThat(atl("/it/car").getCountryPath(), is("/it"));
            Assert.assertThat(atl("/it/car/_").getCountryPath(), is("/it"));
            Assert.assertThat(atl("/it/car/ta").getCountryPath(), is("/it"));
        }

    }

    public static class ParentOf extends ApplicationTenancyLevelTest {

        @Test
        public void whenIs() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").parentOf(ApplicationTenancyLevel.of("/a")), is(true));
            assertThat(ApplicationTenancyLevel.of("/").parentOf(ApplicationTenancyLevel.of("/a/b")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a").parentOf(ApplicationTenancyLevel.of("/a/b")), is(true));
        }
        @Test
        public void whenSame() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").parentOf(ApplicationTenancyLevel.of("/")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a").parentOf(ApplicationTenancyLevel.of("/a")), is(false));
        }
        @Test
        public void whenNot() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/a").parentOf(ApplicationTenancyLevel.of("/")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a/b").parentOf(ApplicationTenancyLevel.of("/")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a/b").parentOf(ApplicationTenancyLevel.of("/a")), is(false));
        }
    }

    public static class ChildOf extends ApplicationTenancyLevelTest {

        @Test
        public void whenNot() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").childOf(ApplicationTenancyLevel.of("/a")), is(false));
            assertThat(ApplicationTenancyLevel.of("/").childOf(ApplicationTenancyLevel.of("/a/b")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a").childOf(ApplicationTenancyLevel.of("/a/b")), is(false));
        }
        @Test
        public void whenSame() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").childOf(ApplicationTenancyLevel.of("/")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a").childOf(ApplicationTenancyLevel.of("/a")), is(false));
        }
        @Test
        public void whenIs() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/a").childOf(ApplicationTenancyLevel.of("/")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a/b").childOf(ApplicationTenancyLevel.of("/")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a/b").childOf(ApplicationTenancyLevel.of("/a")), is(true));
        }
    }

    public static class PeerOf extends ApplicationTenancyLevelTest {

        @Test
        public void whenNot() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").peerOf(ApplicationTenancyLevel.of("/a")), is(false));
            assertThat(ApplicationTenancyLevel.of("/").peerOf(ApplicationTenancyLevel.of("/a/b")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/a/b")), is(false));

            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/a")), is(false));

            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/b")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/a/b/c")), is(false));
        }
        @Test
        public void whenSame() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").peerOf(ApplicationTenancyLevel.of("/")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/a")), is(false));
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/a/b")), is(false));
        }
        @Test
        public void whenIs() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/b")), is(true));

            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/a/c")), is(true));
            assertThat(ApplicationTenancyLevel.of("/b/a").peerOf(ApplicationTenancyLevel.of("/a/a")), is(true));

            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/b/d")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/c/d")), is(true));

            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/b/c")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/b/c/d")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/b/c/d")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/c/d/e")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/c/d/e/f")), is(true));

            assertThat(ApplicationTenancyLevel.of("/b/c").peerOf(ApplicationTenancyLevel.of("/a")), is(true));
            assertThat(ApplicationTenancyLevel.of("/b/c/d").peerOf(ApplicationTenancyLevel.of("/a")), is(true));
            assertThat(ApplicationTenancyLevel.of("/b/c/d").peerOf(ApplicationTenancyLevel.of("/a/b")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a/c/d/e").peerOf(ApplicationTenancyLevel.of("/a/b/c")), is(true));
            assertThat(ApplicationTenancyLevel.of("/a/c/d/e/f").peerOf(ApplicationTenancyLevel.of("/a/b/c")), is(true));
        }
    }

    public static class PredicatesTest extends ApplicationTenancyLevelTest {

        static ApplicationTenancy applicationTenancyOf(final String path) {
            final ApplicationTenancy applicationTenancy = new ApplicationTenancy();
            applicationTenancy.setPath(path);
            return applicationTenancy;
        }

        public static class ParentsOf extends PredicatesTest {

            @Test
            public void whenHasParents() throws Exception {
                //given
                final List<ApplicationTenancy> candidates = Lists.newArrayList(
                        applicationTenancyOf("/"),
                        applicationTenancyOf("/a"),
                        applicationTenancyOf("/x"),
                        applicationTenancyOf("/a/b"),
                        applicationTenancyOf("/a/x"),
                        applicationTenancyOf("/a/b/c"),
                        applicationTenancyOf("/a/b/c/d"));

                final Predicate<ApplicationTenancy> parentsOf = ApplicationTenancyLevel.Predicates.parentsOf(ApplicationTenancyLevel.of("/a/b/c"));

                // when
                final Iterable<ApplicationTenancy> matching = Iterables.filter(candidates, parentsOf);

                // then
                final List<ApplicationTenancy> expected = Lists.newArrayList(
                        candidates.get(0),
                        candidates.get(1),
                        candidates.get(3));
                assertThat(matching, contains(expected.toArray()));
            }

            @Test
            public void whenNoParents() throws Exception {
                //given
                final List<ApplicationTenancy> candidates = Lists.newArrayList(
                        applicationTenancyOf("/"),
                        applicationTenancyOf("/a"),
                        applicationTenancyOf("/x"),
                        applicationTenancyOf("/a/b"),
                        applicationTenancyOf("/a/x"),
                        applicationTenancyOf("/a/b/c"),
                        applicationTenancyOf("/a/b/c/d"));
                final Predicate<ApplicationTenancy> parentsOf = ApplicationTenancyLevel.Predicates.parentsOf(ApplicationTenancyLevel.of("/"));

                // when
                final Iterable<ApplicationTenancy> matching = Iterables.filter(candidates, parentsOf);

                // then
                assertThat(matching, isEmpty());
            }

        }

        public static class ChildrenOf extends PredicatesTest {

            @Test
            public void whenHasChildren() throws Exception {
                //given
                final List<ApplicationTenancy> candidates = Lists.newArrayList(
                        applicationTenancyOf("/"),
                        applicationTenancyOf("/a"),
                        applicationTenancyOf("/b"),
                        applicationTenancyOf("/b/p"),
                        applicationTenancyOf("/a/b"),
                        applicationTenancyOf("/a/c"),
                        applicationTenancyOf("/a/w/x/y"));

                final Predicate<ApplicationTenancy> childrenOf = ApplicationTenancyLevel.Predicates.childrenOf(ApplicationTenancyLevel.of("/a"));

                // when
                final Iterable<ApplicationTenancy> matching = Iterables.filter(candidates, childrenOf);

                // then
                final List<ApplicationTenancy> expected = Lists.newArrayList(
                        candidates.get(4),
                        candidates.get(5),
                        candidates.get(6));
                assertThat(matching, contains(expected.toArray()));
            }

            @Test
            public void whenNoChildren() throws Exception {

                //given
                final List<ApplicationTenancy> candidates = Lists.newArrayList(
                        applicationTenancyOf("/"),
                        applicationTenancyOf("/a"));
                final Predicate<ApplicationTenancy> childrenOf = ApplicationTenancyLevel.Predicates.childrenOf(ApplicationTenancyLevel.of("/a"));

                // when
                final Iterable<ApplicationTenancy> matching = Iterables.filter(candidates, childrenOf);

                // then
                assertThat(matching, isEmpty());
            }

        }

        static Matcher<? super Iterable<ApplicationTenancy>> isEmpty() {
            return new TypeSafeMatcher<Iterable<ApplicationTenancy>>() {
                @Override
                protected boolean matchesSafely(final Iterable<ApplicationTenancy> item) {
                    return !item.iterator().hasNext();
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("is empty");
                }
            };
        }

    }
}