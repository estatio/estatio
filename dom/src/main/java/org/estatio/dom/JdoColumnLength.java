/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

    private JdoColumnLength() {}
    
    public final static int STATUS_ENUM=20;
    public final static int INVOICING_FREQUENCY_ENUM=20;
    public final static int LEASE_TERM_FREQUENCY_ENUM=20;
    public final static int OCCUPANCY_REPORTING_TYPE_ENUM=20;
    public final static int PAYMENT_METHOD_ENUM=20;
    public final static int TYPE_ENUM=20;
    
    public final static int REFERENCE=24;
    
    public final static int TITLE=50;
    public final static int NAME=30;
    public final static int DESCRIPTION=254;
    public final static int NOTES=4000;
    
    public final static int PROPER_NAME=30;
    
    public final static int PHONE_NUMBER=20;
    public final static int EMAIL_ADDRESS=50;

    public final static int FQCN=254;
    public static final int OBJECT_IDENTIFIER = 20;
    public static final int USER_NAME = 50;

    public static final int DURATION = 20;
    
    public static final class Event {
        private Event(){}
        
        public static final int TYPE = 254;
    }
    
    public static final class BreakOption {
        private BreakOption(){}
        
        public static final int EXERCISE_TYPE_ENUM = 20;
    }

    public static final class PostalAddress {
        private PostalAddress(){}
        
        public static final int ADDRESS_LINE = 100;
        public static final int POSTAL_CODE = 12;
    }
    
    public static final class LeaseTermForTurnoverRent {
        private LeaseTermForTurnoverRent(){}
        
        public static final int RENT_RULE = 254;
    }

    
    public static final class Numerator {
        private Numerator(){}
        
        /**
         * {@link JdoColumnLength#REFERENCE} plus a few chars
         */
        public final static int FORMAT=30; 
    }
    

    public static final class Invoice {
        private Invoice(){}
        
        /**
         * TODO: review
         */
        public static final int NUMBER = 16;
    }

    public static final class Organisation {
        private Organisation(){}
        /**
         * TODO: review
         */
        public static final int FISCAL_CODE = 30;
        /**
         * TODO: review
         */
        public static final int VAT_CODE = 30;
    }
    
    public static final class Charge {
        private Charge(){}
        /**
         * TODO: review
         */
        public static final int CODE = 30;
    }
    
    public static final class Country {
        private Country(){}
        public static final int ALPHA2CODE = 2;
    }
    
    public static final class Person {
        private Person(){}
        public static final int INITIALS = 3;
    }
    
    public final static class BankAccount {
        private BankAccount() {}

        /**
         * eg http://en.wikipedia.org/wiki/International_Bank_Account_Number
         * 
         * max length currently is Malta, 31 chars.
         */
        public final static int IBAN=32;

        /**
         * TODO: review
         */
        public final static int ACCOUNT_NUMBER=20;
        /**
         * TODO: review
         */
        public final static int BRANCH_CODE=20;
        /**
         * TODO: review
         */
        public final static int NATIONAL_BANK_CODE=20;
        /**
         * TODO: review
         */
        public final static int NATIONAL_CHECK_CODE=20;
    }

    public final static class State {
        private State() {}

        public final static int REFERENCE = 6;
    }

}
