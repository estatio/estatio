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
package org.estatio.module.bankaccount.dom.utils;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.bankaccount.dom.BankAccount;

public final class IBANHelper {

    private IBANHelper() {
    }

    public enum IBANFormat {
        AL("ALkk aaas sssx cccc cccc cccc cccc"),
        AD("ADkk aaaa ssss cccc cccc cccc"),
        AT("ATkk aaaa accc cccc cccc"),
        AZ("AZkk aaaa cccc cccc cccc cccc cccc"),
        BH("BHkk aaaa cccc cccc cccc cc"),
        BE("BEkk aaac cccc ccxx"),
        BA("BA39 aaas sscc cccc ccxx"),
        BR("BR39 aaaa aaaa ssss sccc cccc ccct n"),
        BG("BGkk aaaa ssss ddcc cccc cc"),
        BF("BFkkk sssss cccccccccccc dd"),
        CR("CRkk aaac cccc cccc cccc c"),
        HR("HRkk aaaa aaac cccc cccc c"),
        CY("CYkk aaas ssss cccc cccc cccc cccc"),
        CZ("CZkk aaaa ssss sscc cccc cccc"),
        DK("DKkk aaaa cccc cccc cc"),
        DO("DOkk aaaa cccc cccc cccc cccc cccc"),
        EE("EEkk aass cccc cccc cccx"),
        FO("FOkk aaaa cccc cccc cx"),
        FI("FIkk aaaa aacc cccc cx"),
        FR("FRkk aaaa aggg ggcc cccc cccc cxx"),
        GE("GEkk aacc cccc cccc cccc cc"),
        DE("DEkk aaaa aaaa cccc cccc cc"),
        GI("GIkk aaaa cccc cccc cccc ccc"),
        GR("GRkk aaas sssc cccc cccc cccc ccc"),
        GL("GLkk aaaa cccc cccc cc"),
        GT("GTkk aaaa cccc cccc cccc cccc cccc"),
        HU("HUkk aaas sssk cccc cccc cccc cccx"),
        IS("ISkk aaaa sscc cccc iiii iiii ii"),
        IE("IEkk aaaa bbbb bbcc cccc cc"),
        IL("ILkk aaan nncc cccc cccc ccc"),
        IT("ITkk xaaa aabb bbbc cccc cccc ccc"),
        KZ("KZkk aaac cccc cccc cccc"),
        KW("KWkk aaaa cccc cccc cccc cccc cccc cc"),
        LV("LVkk aaaa cccc cccc cccc c"),
        LB("LBkk aaaa cccc cccc cccc cccc cccc"),
        LI("LIkk aaaa accc cccc cccc c"),
        LT("LTkk aaaa accc cccc cccc"),
        LU("LUkk aaac cccc cccc cccc"),
        MK("MK07 aaac cccc cccc cxx"),
        MT("MTkk aaaa ssss sccc cccc cccc cccc ccc"),
        MR("MR13 aaaa asss sscc cccc cccc cxx"),
        MU("MUkk aaaa aass cccc cccc cccc cccc cc"),
        MC("MCkk aaaa asss sscc cccc cccc cxx"),
        MD("MDkk aacc cccc cccc cccc cccc"),
        ME("ME25 aaac cccc cccc cccc xx"),
        NL("NLkk aaaa cccc cccc cc"),
        NO("NOkk aaaa cccc ccx"),
        PK("PKkk aaaa cccc cccc cccc cccc"),
        PS("PSkk aaaa xxxx xxxx xccc cccc cccc c"),
        PL("PLkk aaas sssx cccc cccc cccc cccc"),
        PT("PT50 aaaa ssss cccc cccc cccx x"),
        RO("ROkk aaaa cccc cccc cccc cccc"),
        SM("SMkk xaaa aabb bbbc cccc cccc ccc"),
        SA("SAkk aacc cccc cccc cccc cccc"),
        RS("RSkk aaac cccc cccc cccc xx"),
        SK("SKkk aaaa ssss sscc cccc cccc"),
        SI("SI56 aass sccc cccc cxx"),
        ES("ESkk aaaa gggg xxcc cccc cccc"),
        SE("SEkk aaac cccc cccc cccc cccc"),
        CH("CHkk aaaa accc cccc cccc c"),
        TN("TN59 aass sccc cccc cccc cccc"),
        TR("TRkk aaaa axcc cccc cccc cccc cc"),
        AE("AEkk aaac cccc cccc cccc ccc"),
        GB("GBkk aaaa ssss sscc cccc cc"),
        VG("VGkk aaaa cccc cccc cccc cccc");

        private String format;

        private IBANFormat(final String format) {
            this.format = format;
        }

        public String format() {
            return format.replaceAll("\\s+", "");
        }
    }

    public static void verifyAndUpdate(final BankAccount account) {
        if (!IBANValidator.valid(account.getIban())) {
            // not a valid account, see if we can fetch
            assembleIBAN(account);
        } else {
            // break into separate parts
            disassembleIBAN(account);
        }
    }

    public static void disassembleIBAN(final BankAccount account) {
        String iban = account.getIban();
        IBANFormat format = IBANFormat.valueOf(iban.substring(0, 2));
        if (format != null && iban.length() == format.format().length()) {
            account.setNationalBankCode(partWithCharacter(format.format(), iban, "a"));
            account.setNationalCheckCode(partWithCharacter(format.format(), iban, "x"));
            account.setBranchCode(partWithCharacter(format.format(), iban, "b"));
            account.setAccountNumber(partWithCharacter(format.format(), iban, "c"));
        }
    }

    public static void assembleIBAN(final BankAccount account) {
        Country country = account.getCountry();
        if (country != null) {
            IBANFormat format = IBANFormat.valueOf(country.getAlpha2Code());
            if (format != null && account.getAccountNumber() != null) {
                String iban = format.format();
                iban = injectPartWithCharacter(iban, "x", account.getNationalCheckCode());
                iban = injectPartWithCharacter(iban, "a", account.getNationalBankCode());
                iban = injectPartWithCharacter(iban, "b", account.getBranchCode());
                iban = injectPartWithCharacter(iban, "c", account.getAccountNumber());
                iban = iban.replace("kk", "00");
                if (iban.length() != format.format().length()) {
                    return;
                }
                if (IBANValidator.valid(iban)) {
                    account.setIban(iban);
                }
            }
        }
    }

    private static String partWithCharacter(
            final String format, final String iban, final String character) {
        int beginIndex = format.indexOf(character);
        int endIndex = format.lastIndexOf(character);
        if (beginIndex > -1 && endIndex >= beginIndex) {
            return iban.substring(beginIndex, endIndex + 1);
        }
        return "";
    }

    private static String injectPartWithCharacter(
            final String ibanFormat,
            final String character,
            final String replacement) {
        int beginIndex = ibanFormat.indexOf(character);
        int endIndex = ibanFormat.lastIndexOf(character);
        String pattern = "";
        if (beginIndex > -1 && endIndex >= beginIndex) {
            pattern = ibanFormat.substring(beginIndex, endIndex + 1);
            return ibanFormat.replace(pattern, replacement == null ? "" : replacement);
        } else {
            return ibanFormat;
        }
    }

}