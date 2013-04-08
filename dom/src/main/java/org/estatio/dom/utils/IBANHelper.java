package org.estatio.dom.utils;

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
    private Boolean isValid = false;

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
        return isValid();
    }

    private void initialize() {
        IBANValidator ibanValidator = new IBANValidator();
        if (ibanValidator.valid(iban)) {
            if (format == null && iban.length() > 2) {
                countryCode = iban.substring(0, 2);
                format = IBANFormat.valueOf(countryCode).format();
            }
            format = format.replaceAll("\\s", "");
            if (format.length() == iban.length()) {
                nationalBankCode = partWithCharacter("a");
                nationalCheckCode = partWithCharacter("x");
                branchCode = partWithCharacter("b");
                accountNumber = partWithCharacter("c");
                isValid = true;
            }
        }
    }

    public void update(BankAccount account) {
        if (this.isValid) {
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