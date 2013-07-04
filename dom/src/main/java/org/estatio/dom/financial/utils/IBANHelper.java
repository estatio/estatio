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
package org.estatio.dom.financial.utils;

import org.estatio.dom.financial.BankAccount;

public class IBANHelper {

    public enum IBANFormat {
        IT("ITkk xaaa aabb bbbc cccc cccc ccc"), 
        NL("NLkk aaaa cccc cccc cc");

        private String format;

        private IBANFormat(String format) {
            this.format = format;
        }

        public String format() {
            return format.replaceAll("\\s", "");
        }
    }

    private String format;
    private String iban;
    private String nationalBankCode;
    private String nationalCheckCode;
    private String accountNumber;
    private String branchCode;
    private String countryCode;
    private Boolean valid = false;

    public IBANHelper(String iban, String format) {
        this.format = format;
        this.iban = iban;
        initialize();
    }

    public IBANHelper(String iban) {
        this.iban = iban;
        initialize();
    }

    public boolean isValid() {
        return valid;
    }

    private void initialize() {
        IBANValidator ibanValidator = new IBANValidator();
        if (ibanValidator.valid(iban)) {
            if (format == null && iban.length() > 2) {
                countryCode = iban.substring(0, 2);
                format = IBANFormat.valueOf(countryCode).format();
            }
            // TODO: findbugs flags an issue here...
            // what if iban.length() < 2 and format == null; will get NPE
            format = format.replaceAll("\\s", "");
            if (format.length() == iban.length()) {
                nationalBankCode = partWithCharacter("a");
                nationalCheckCode = partWithCharacter("x");
                branchCode = partWithCharacter("b");
                accountNumber = partWithCharacter("c");
                valid = true;
            }
        }
    }

    public void update(BankAccount account) {
        if (valid) {
            account.setAccountNumber(accountNumber);
            account.setBranchCode(branchCode);
            account.setNationalBankCode(nationalBankCode);
            account.setNationalCheckCode(nationalCheckCode);
            // account.setCountry(country);
        }
    }

    private String partWithCharacter(String character) {
        int beginIndex = format.indexOf(character);
        int endIndex = format.lastIndexOf(character);
        if (beginIndex > -1 && endIndex >= beginIndex) {
            return iban.substring(beginIndex, endIndex + 1);
        }
        return "";
    }
}