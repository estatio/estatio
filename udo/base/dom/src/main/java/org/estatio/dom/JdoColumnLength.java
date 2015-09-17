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

public final class JdoColumnLength {

    private JdoColumnLength() {
    }

    public final static int STATUS_ENUM = 20;
    public final static int INVOICING_FREQUENCY_ENUM = 30;
    public final static int LEASE_TERM_FREQUENCY_ENUM = 30;
    public final static int OCCUPANCY_REPORTING_TYPE_ENUM = 30;
    public final static int PAYMENT_METHOD_ENUM = 30;
    public final static int TYPE_ENUM = 30;

    public final static int REFERENCE = 24;

    public final static int TITLE = 50;
    public final static int NAME = 50;
    public final static int SHORT_DESCRIPTION = 50;
    public final static int DESCRIPTION = 254;
    public final static int NOTES = 4000;

    public final static int PROPER_NAME = 50;

    public final static int PHONE_NUMBER = 20;

    public final static int EMAIL_ADDRESS = 254; //http://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address 

    public final static int FQCN = 254;
    public static final int OBJECT_IDENTIFIER = 20;
    public static final int USER_NAME = 50;

    public static final int DURATION = 20;

    public static final class Setting {
        public final static int KEY = 128;
        public final static int TYPE = 20;
    }

    public static final class Link {
        private Link() {
        }

        public static final int URL_TEMPLATE = 254;
    }

    public static final class EstatioInstance {
        private EstatioInstance() {
        }

        public static final int PATH = 12;
    }

    public static final class Event {
        private Event() {
        }

        public static final int CALENDAR_NAME = 254;
    }

    public static final class BreakOption {
        private BreakOption() {
        }

        public static final int EXERCISE_TYPE_ENUM = 20;
    }

    public static final class PostalAddress {
        private PostalAddress() {
        }

        public static final int ADDRESS_LINE = 100;
        public static final int POSTAL_CODE = 12;
    }

    public static final class LeaseTermForTurnoverRent {
        private LeaseTermForTurnoverRent() {
        }

        public static final int RENT_RULE = 254;
    }

    public static final class Numerator {
        private Numerator() {
        }

        /**
         * {@link JdoColumnLength#REFERENCE} plus a few chars
         */
        public final static int FORMAT = 30;
    }

    public static final class Invoice {
        private Invoice() {
        }

        /**
         * TODO: review
         */
        public static final int NUMBER = 16;
    }

    public static final class Organisation {
        private Organisation() {
        }

        /**
         * TODO: review
         */
        public static final int FISCAL_CODE = 30;
        /**
         * TODO: review
         */
        public static final int VAT_CODE = 30;
    }

    public static final class Country {
        private Country() {
        }

        public static final int ALPHA2CODE = 2;
    }

    public static final class Party {
        private Party() {
        }

        public static final int NAME = 80;
    }

    public static final class Person {
        private Person() {
        }

        public static final int INITIALS = 3;
    }

    public final static class FinancialAccount {
        private FinancialAccount() {
        }

        /**
         * To store the IBAN code as reference we need to increase this
         * 
         */
        public final static int REFERENCE = 34;
    }

    public final static class BankAccount {
        private BankAccount() {
        }

        /**
         * eg http://en.wikipedia.org/wiki/International_Bank_Account_Number
         * 
         */
        public final static int IBAN = 34;

        /**
         * TODO: review
         */
        public final static int ACCOUNT_NUMBER = 20;
        /**
         * TODO: review
         */
        public final static int BRANCH_CODE = 20;
        /**
         * TODO: review
         */
        public final static int NATIONAL_BANK_CODE = 20;
        /**
         * TODO: review
         */
        public final static int NATIONAL_CHECK_CODE = 20;
    }

    public final static class BankMandate {
        private BankMandate() {
        }

        public final static int SEPA_MANDATE_IDENTIFIER = 35;

    }

    public final static class State {
        private State() {
        }

        public final static int REFERENCE = 6;
    }

}
