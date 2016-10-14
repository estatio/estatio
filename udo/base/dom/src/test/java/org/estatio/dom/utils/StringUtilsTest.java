/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.utils;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.incode.module.base.dom.PrivateConstructorTester;

import static org.junit.Assert.assertEquals;


public class StringUtilsTest {

    public static class constructor extends StringUtilsTest {
        @Test
        public void instantiate() throws Exception {
            new PrivateConstructorTester(StringUtils.class).exercise();
        }
    }

    @RunWith(Parameterized.class)
    public static class EnumDeTitle extends StringUtilsTest {

        private String from;
        private String to;

        @Parameterized.Parameters
        public static Collection<Object[]> values() {
            return Arrays.asList(
                    new Object[][]{
                            {"Foo", "FOO"},
                            {"Foo Bar", "FOO_BAR"},
                            {null, null},
                    }
            );
        }

        public EnumDeTitle(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Test
        public void nonNull() throws Exception {
            assertEquals(to, StringUtils.enumDeTitle(from));
        }

    }

    @RunWith(Parameterized.class)
    public static class EnumTitle extends StringUtilsTest {

        private String from;
        private String to;

        @Parameterized.Parameters
        public static Collection<Object[]> values() {
            return Arrays.asList(
                    new Object[][]{
                            {"FOO", "Foo"},
                            {"FOO_BAR", "Foo Bar"},
                            {null, null},
                    }
            );
        }

        public EnumTitle(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Test
        public void nonNull() throws Exception {
            assertEquals(to, StringUtils.enumTitle(from));
        }

    }

    @RunWith(Parameterized.class)
    public static class WildcardToCaseInsensitiveRegex extends StringUtilsTest {

        private String from;
        private String to;

        @Parameterized.Parameters
        public static Collection<Object[]> values() {
            return Arrays.asList(
                    new Object[][]{
                            {"*abc?def*ghi", "(?i).*abc.def.*ghi"},
                            {null, null},
                    }
            );
        }

        public WildcardToCaseInsensitiveRegex(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Test
        public void nonNull() throws Exception {
            assertEquals(to, StringUtils.wildcardToCaseInsensitiveRegex(from));
        }
    }

    @RunWith(Parameterized.class)
    public static class WildcardToRegex extends StringUtilsTest {

        private String from;
        private String to;

        @Parameterized.Parameters
        public static Collection<Object[]> values() {
            return Arrays.asList(
                    new Object[][]{
                            {"*abc?def*ghi", ".*abc.def.*ghi"},
                            {null, null},
                    }
            );
        }

        public WildcardToRegex(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Test
        public void nonNull() throws Exception {
            assertEquals(to, StringUtils.wildcardToRegex(from));
        }

    }

}