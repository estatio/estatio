package org.estatio.dom.instance;

import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class HierarchyTest {

    static Hierarchy ih(String path) {
        return new Hierarchy(path);
    }

    public static class ParentOf_and_ChildOf extends HierarchyTest {

        static void assertXxxOf(String target, String candidate, boolean expected) {
            assertThat(ih(target).parentOf(ih(candidate)), is(expected));
            assertThat(ih(candidate).childOf(ih(target)), is(expected));
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
            final Hierarchy ih = ih("/");
            final Hierarchy ih2 = ih("/a/bb");

            assertThat(ih.parentOf(ih), is(false));
            assertThat(ih2.parentOf(ih2), is(false));
        }

        @Test
        public void sadCase_whenEqual() throws Exception {
            assertXxxOf("/", "/", false);
            assertXxxOf("/a", "/a", false);
            assertXxxOf("/a/bb", "/a/bb", false);
        }

        @Test
        public void sadCase_parentOf_whenNull() throws Exception {
            assertThat(ih("/").parentOf(null), is(false));
        }

        @Test
        public void sadCase_childOf_whenNull() throws Exception {
            assertThat(ih("/").childOf(null), is(false));
        }
    }

    public static class GetParts extends HierarchyTest {

        @Test
        public void happyCase() throws Exception {
            final List<String> parts = ih("/a/bb").getParts();

            assertThat(parts.size(), is(2));
            assertThat(parts.get(0), is("a"));
            assertThat(parts.get(1), is("bb"));
        }

        @Test
        public void root() throws Exception {
            final List<String> parts = ih("/").getParts();

            assertThat(parts.size(), is(0));
        }
    }

    public static class Parent extends HierarchyTest {

        @Test
        public void happyCase() throws Exception {
            assertThat(ih("/a/bb").parent().toString(), is("/a"));
        }

        @Test
        public void root() throws Exception {
            assertThat(ih("/").parent(), is(nullValue()));
        }
    }

}