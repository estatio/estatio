package org.estatio.dom.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class AtPathUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class ToRegExSearch extends AtPathUtilsTest {

        @Test
        public void when_all_of_em() throws Exception {

            assertToRegexSearch(null, 2, "/null/null");    // this will match nothing
            assertToRegexSearch("/", 1, "/.*");
            assertToRegexSearch("/", 2, "/.*/.*");
            assertToRegexSearch("/IT", 1, "/IT");
            assertToRegexSearch("/IT", 2, "/IT/.*");
            assertToRegexSearch("/IT/CAR", 2, "/IT/CAR");
            assertToRegexSearch("/IT/CAR", 3, "/IT/CAR/.*");
            assertToRegexSearch("/IT/CAR/IT01", 3, "/IT/CAR/IT01");
        }

        @Test
        public void cant_have_0_parts() throws Exception {

            expectedException.expect(IllegalArgumentException.class);
            assertToRegexSearch("/", 0, "/");
        }

        private static void assertToRegexSearch(final String atPath, final int parts, final String expected) {
            assertThat(AtPathUtils.toAtPathRegex(atPath, parts)).isEqualTo(expected);
        }

    }

    public static class ToCountryCode extends AtPathUtilsTest {

        @Test
        public void all_of_em() {
            assertToCountryCode(null, "null");
            assertToCountryCode("/", ".*");
            assertToCountryCode("/ITA", "ITA");
            assertToCountryCode("/ITA/CAR", "ITA");
            assertToCountryCode("/ITA/CAR/IT01", "ITA");
        }

        private static void assertToCountryCode(final String atPath, final String expected) {
            assertThat(AtPathUtils.toCountryRefRegex(atPath)).isEqualTo(expected);
        }
    }



}