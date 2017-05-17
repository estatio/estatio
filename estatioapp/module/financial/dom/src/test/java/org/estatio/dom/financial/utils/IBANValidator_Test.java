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
package org.estatio.dom.financial.utils;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IBANValidator_Test {

    public static class Valid extends IBANValidator_Test {

        @Before
        public void setUp() throws Exception {
        }

        @Test
        public void happyCase() {
            assertThat(IBANValidator.valid("NL26INGB0680433600")).isTrue();
            assertThat(IBANValidator.valid("NL07INGB0697694704")).isTrue();
            assertThat(IBANValidator.valid("IT69N0347501601000051986922")).isTrue();
            assertThat(IBANValidator.valid("NL68RABO0145568962")).isTrue();
            assertThat(IBANValidator.valid("IT93Q0347501601000051768165")).isTrue();
        }

        @Test
        public void sadCase() {
            assertThat(IBANValidator.valid("NLXXINGB0680433600")).isFalse();
            assertThat(IBANValidator.valid("nl26INGB0680433600")).isFalse();
            assertThat(IBANValidator.valid("NL26iNGB0680433600")).isFalse();
            assertThat(IBANValidator.valid("")).isFalse();
            assertThat(IBANValidator.valid("rubbish")).isFalse();
        }

    }

    public static class Checksum extends IBANValidator_Test {

        @Test
        public void happyCase() {
            assertThat(IBANValidator.checksum("IT93Q0347501601000051768165")).isEqualTo(1);
        }

    }

    public static class FixChecksum extends IBANValidator_Test {

        @Test
        public void happyCase() {
            assertThat(IBANValidator.fixChecksum("IT00Q0347501601000051768165")).isEqualTo("IT93Q0347501601000051768165");
        }

    }
}