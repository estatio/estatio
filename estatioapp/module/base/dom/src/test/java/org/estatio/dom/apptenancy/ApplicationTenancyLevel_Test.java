package org.estatio.dom.apptenancy;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTenancyLevel_Test {

    static ApplicationTenancyLevel atl(String path) {
        return new ApplicationTenancyLevel(path);
    }

    public static class ParentOf_and_ChildOf extends ApplicationTenancyLevel_Test {

        static void assertXxxOf(String target, String candidate, boolean expected) {
            assertThat(atl(target).parentOf(atl(candidate))).isEqualTo(expected);
            assertThat(atl(candidate).childOf(atl(target))).isEqualTo(expected);
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

            assertThat(ih.parentOf(ih)).isFalse();
            assertThat(ih2.parentOf(ih2)).isFalse();
        }

        @Test
        public void sadCase_whenEqual() throws Exception {
            assertXxxOf("/", "/", false);
            assertXxxOf("/a", "/a", false);
            assertXxxOf("/a/bb", "/a/bb", false);
        }

        @Test
        public void sadCase_parentOf_whenNull() throws Exception {
            assertThat(atl("/").parentOf(null)).isFalse();
        }

        @Test
        public void sadCase_childOf_whenNull() throws Exception {
            assertThat(atl("/").childOf(null)).isFalse();
        }
    }

    public static class GetParts extends ApplicationTenancyLevel_Test {

        @Test
        public void happyCase() throws Exception {
            final List<String> parts = atl("/a/bb").getParts();

            assertThat(parts).hasSize(2);
            assertThat(parts.get(0)).isEqualTo("a");
            assertThat(parts.get(1)).isEqualTo("bb");
        }

        @Test
        public void root() throws Exception {
            final List<String> parts = atl("/").getParts();

            assertThat(parts).hasSize(0);
        }
    }

    public static class Parent extends ApplicationTenancyLevel_Test {

        @Test
        public void happyCase() throws Exception {
            assertThat(atl("/a/bb").parent().toString()).isEqualTo("/a");
        }

        @Test
        public void root() throws Exception {
            assertThat(atl("/").parent()).isNull();
        }
    }

    public static class Child extends ApplicationTenancyLevel_Test {

        @Test
        public void happyCase() throws Exception {
            assertThat(atl("/a").child("bb").toString()).isEqualTo("/a/bb");
        }

        @Test
        public void root() throws Exception {
            assertThat(atl("/").child("_")).isEqualTo(atl("/_"));
        }

        @Test(expected = IllegalArgumentException.class)
        public void containsIllegalChars() throws Exception {
            atl("/").child("_/_");
        }
    }

    public static class IsXxx extends ApplicationTenancyLevel_Test {

        @Test
        public void isRoot() throws Exception {
            assertThat(atl("/").isRoot()).isTrue();
            assertThat(atl("/_").isRoot()).isFalse();
            assertThat(atl("/it").isRoot()).isFalse();
            assertThat(atl("/it/_").isRoot()).isFalse();
            assertThat(atl("/it/car").isRoot()).isFalse();
            assertThat(atl("/it/car/_").isRoot()).isFalse();
            assertThat(atl("/it/car/ta").isRoot()).isFalse();
        }
        @Test
        public void isRootOther() throws Exception {
            assertThat(atl("/").isRootOther()).isFalse();
            assertThat(atl("/_").isRootOther()).isTrue();
            assertThat(atl("/it").isRootOther()).isFalse();
            assertThat(atl("/it/_").isRootOther()).isFalse();
            assertThat(atl("/it/car").isRootOther()).isFalse();
            assertThat(atl("/it/car/_").isRootOther()).isFalse();
            assertThat(atl("/it/car/ta").isRootOther()).isFalse();
        }
        @Test
        public void isCountry() throws Exception {
            assertThat(atl("/").isCountry()).isFalse();
            assertThat(atl("/_").isCountry()).isFalse();
            assertThat(atl("/it").isCountry()).isTrue();
            assertThat(atl("/it/_").isCountry()).isFalse();
            assertThat(atl("/it/car").isCountry()).isFalse();
            assertThat(atl("/it/car/_").isCountry()).isFalse();
            assertThat(atl("/it/car/ta").isCountry()).isFalse();
        }
        @Test
        public void isCountryOnly() throws Exception {
            assertThat(atl("/").isCountryOther()).isFalse();
            assertThat(atl("/_").isCountryOther()).isFalse();
            assertThat(atl("/it").isCountryOther()).isFalse();
            assertThat(atl("/it/_").isCountryOther()).isTrue();
            assertThat(atl("/it/car").isCountryOther()).isFalse();
            assertThat(atl("/it/car/_").isCountryOther()).isFalse();
            assertThat(atl("/it/car/ta").isCountryOther()).isFalse();
        }
        @Test
        public void isProperty() throws Exception {
            assertThat(atl("/").isProperty()).isFalse();
            assertThat(atl("/_").isProperty()).isFalse();
            assertThat(atl("/it").isProperty()).isFalse();
            assertThat(atl("/it/_").isProperty()).isFalse();
            assertThat(atl("/it/car").isProperty()).isTrue();
            assertThat(atl("/it/car/_").isProperty()).isFalse();
            assertThat(atl("/it/car/ta").isProperty()).isFalse();
        }
        @Test
        public void isLocalDefault() throws Exception {
            assertThat(atl("/").isLocalDefault()).isFalse();
            assertThat(atl("/_").isLocalDefault()).isFalse();
            assertThat(atl("/it").isLocalDefault()).isFalse();
            assertThat(atl("/it/_").isLocalDefault()).isFalse();
            assertThat(atl("/it/car").isLocalDefault()).isFalse();
            assertThat(atl("/it/car/_").isLocalDefault()).isTrue();
            assertThat(atl("/it/car/ta").isLocalDefault()).isFalse();
        }
        @Test
        public void isLocalNamed() throws Exception {
            assertThat(atl("/").isLocalNamed("ta")).isFalse();
            assertThat(atl("/_").isLocalNamed("ta")).isFalse();
            assertThat(atl("/it").isLocalNamed("ta")).isFalse();
            assertThat(atl("/it/_").isLocalNamed("ta")).isFalse();
            assertThat(atl("/it/car").isLocalNamed("ta")).isFalse();
            assertThat(atl("/it/car/_").isLocalNamed("ta")).isFalse();
            assertThat(atl("/it/car/ta").isLocalNamed("ta")).isTrue();
        }
        @Test
        public void isPropertyOf() throws Exception {
            assertThat(atl("/").isPropertyOf(atl("/it"))).isFalse();
            assertThat(atl("/it").isPropertyOf(atl("/it"))).isFalse();
            assertThat(atl("/it/_").isPropertyOf(atl("/it"))).isFalse();
            assertThat(atl("/it/car").isPropertyOf(atl("/it"))).isTrue();
            assertThat(atl("/fr/car").isPropertyOf(atl("/it"))).isFalse();
            assertThat(atl("/it/car/_").isPropertyOf(atl("/it"))).isFalse();
            assertThat(atl("/it/car/ta").isPropertyOf(atl("/it"))).isFalse();
        }

    }

    public static class GetCountryPath extends ApplicationTenancyLevel_Test {

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
            assertThat(atl("/it").getCountryPath()).isEqualTo("/it");
            assertThat(atl("/it/_").getCountryPath()).isEqualTo("/it");
            assertThat(atl("/it/car").getCountryPath()).isEqualTo("/it");
            assertThat(atl("/it/car/_").getCountryPath()).isEqualTo("/it");
            assertThat(atl("/it/car/ta").getCountryPath()).isEqualTo("/it");
        }

    }

    public static class ParentOf extends ApplicationTenancyLevel_Test {

        @Test
        public void whenIs() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").parentOf(ApplicationTenancyLevel.of("/a"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/").parentOf(ApplicationTenancyLevel.of("/a/b"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a").parentOf(ApplicationTenancyLevel.of("/a/b"))).isTrue();
        }
        @Test
        public void whenSame() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").parentOf(ApplicationTenancyLevel.of("/"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a").parentOf(ApplicationTenancyLevel.of("/a"))).isFalse();
        }
        @Test
        public void whenNot() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/a").parentOf(ApplicationTenancyLevel.of("/"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a/b").parentOf(ApplicationTenancyLevel.of("/"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a/b").parentOf(ApplicationTenancyLevel.of("/a"))).isFalse();
        }
    }

    public static class ChildOf extends ApplicationTenancyLevel_Test {

        @Test
        public void whenNot() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").childOf(ApplicationTenancyLevel.of("/a"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/").childOf(ApplicationTenancyLevel.of("/a/b"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a").childOf(ApplicationTenancyLevel.of("/a/b"))).isFalse();
        }
        @Test
        public void whenSame() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").childOf(ApplicationTenancyLevel.of("/"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a").childOf(ApplicationTenancyLevel.of("/a"))).isFalse();
        }
        @Test
        public void whenIs() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/a").childOf(ApplicationTenancyLevel.of("/"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a/b").childOf(ApplicationTenancyLevel.of("/"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a/b").childOf(ApplicationTenancyLevel.of("/a"))).isTrue();
        }
    }

    public static class PeerOf extends ApplicationTenancyLevel_Test {

        @Test
        public void whenNot() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").peerOf(ApplicationTenancyLevel.of("/a"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/").peerOf(ApplicationTenancyLevel.of("/a/b"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/a/b"))).isFalse();

            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/a"))).isFalse();

            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/b"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/a/b/c"))).isFalse();
        }
        @Test
        public void whenSame() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/").peerOf(ApplicationTenancyLevel.of("/"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/a"))).isFalse();
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/a/b"))).isFalse();
        }
        @Test
        public void whenIs() throws Exception {
            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/b"))).isTrue();

            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/a/c"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/b/a").peerOf(ApplicationTenancyLevel.of("/a/a"))).isTrue();

            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/b/d"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/c/d"))).isTrue();

            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/b/c"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a").peerOf(ApplicationTenancyLevel.of("/b/c/d"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a/b").peerOf(ApplicationTenancyLevel.of("/b/c/d"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/c/d/e"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a/b/c").peerOf(ApplicationTenancyLevel.of("/a/c/d/e/f"))).isTrue();

            assertThat(ApplicationTenancyLevel.of("/b/c").peerOf(ApplicationTenancyLevel.of("/a"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/b/c/d").peerOf(ApplicationTenancyLevel.of("/a"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/b/c/d").peerOf(ApplicationTenancyLevel.of("/a/b"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a/c/d/e").peerOf(ApplicationTenancyLevel.of("/a/b/c"))).isTrue();
            assertThat(ApplicationTenancyLevel.of("/a/c/d/e/f").peerOf(ApplicationTenancyLevel.of("/a/b/c"))).isTrue();
        }
    }

    public static class PredicatesTest extends ApplicationTenancyLevel_Test {

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
                assertThat(matching).containsAll(expected);
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
                assertThat(matching).isEmpty();
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
                assertThat(matching).containsAll(expected);
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
                assertThat(matching).isEmpty();
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