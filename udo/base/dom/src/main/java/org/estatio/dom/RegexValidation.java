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
package org.estatio.dom;

public final class RegexValidation {

    public static final String REFERENCE = "[ -/_A-Z0-9]+";
    public static final String REFERENCE_DESCRIPTION = "Only capital letters, numbers and 3 symbols being: \"_\" , \"-\" and \"/\" are allowed";

    public static final class Currency {
        private Currency() {
        }

        public static final String REFERENCE = "[A-Z]+";
        public static final String REFERENCE_DESCRIPTION = "Only letters are allowed";
    }

    public static final class Person {
        private Person() {
        }

        public static final String REFERENCE = "[A-Z,0-9,_,-,/]+";
        public static final String REFERENCE_DESCRIPTION = "Only letters, numbers and 3 symbols being: \"_\" , \"-\" and \"/\" are allowed";
        public static final String INITIALS = "[A-Z]+";
        public static final String INITIALS_DESCRIPTION = "Only letters are allowed";
    }

    public static final class Property {
        private Property() {
        }

        /* Only 3 letters */
        public static final String REFERENCE = "[A-Z,0-9]{2,4}";
        public static final String REFERENCE_DESCRIPTION = "2 to 4 numbers or letters, e.g. XXX9";
    }

    public static final class BankAccount {
        private BankAccount() {
        }

        public static final String IBAN = "[A-Z,0-9]+";
        public static final String IBAN_DESCRIPTION = "Only letters and numbers are allowed";
    }

    public static final class CommunicationChannel {
        private CommunicationChannel() {
        }

        public static final String PHONENUMBER = "[+]?[0-9 -]*";
        public static final String PHONENUMBER_DESCRIPTION = "Only numbers and two symbols being \"-\" and \"+\" are allowed ";

        // as per http://emailregex.com/
        // better would probably be:
        // (?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])
        public static final String EMAIL = "[^@ ]*@{1}[^@ ]*[.]+[^@ ]*";
        public static final String EMAIL_DESCRIPTION = "Only one \"@\" symbol is allowed, followed by a domain e.g. test@example.com";
    }

    public static final class Lease {
        private Lease() {
        }

        //(?=(?:.{11,15}|.{17}))([X,Z]{1}-)?([A-Z]{3}-([A-Z,0-9]{3,8})-[A-Z,0-9,\&+=_/-]{1,7})
        //public static final String REFERENCE = "(?=.{11,17})([A-Z]{1}-)?([A-Z]{3}-([A-Z,0-9]{3,8})-[A-Z,0-9,\\&+=_/-]{1,7})";
        //public static final String REFERENCE = "^([X,Z]-)?(?=.{11,15}$)([A-Z]{3})-([A-Z,0-9]{3,8})-([A-Z,0-9,\\&+=_/-]{1,7})$";
        public static final String REFERENCE = "^([X,Z]-)?(?=.{8,15}$)([A-Z]{2,4})-([A-Z,0-9,\\&\\ \\+=_/-]{1,15})$";
        public static final String REFERENCE_DESCRIPTION = "Only letters and numbers devided by at least 2 and at most 4 dashes:\"-\" totalling between 8 and 15 characters. ";
    }

    public static final class Unit {
        private Unit() {
        }

        public static final String REFERENCE = "(?=.{5,17})([A-Z]{1}-)?([A-Z]{2,4}-[A-Z,0-9,/,+,-]{1,11})";
        public static final String REFERENCE_DESCRIPTION = "Only letters and numbers devided by at least 1 and at most 3 dashes:\"-\" totalling between 5 and 15 characters. ";
    }
}