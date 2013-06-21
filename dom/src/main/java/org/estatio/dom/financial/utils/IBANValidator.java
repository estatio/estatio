package org.estatio.dom.financial.utils;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;


public class IBANValidator {

    private static final BigDecimal NINETYSEVEN = BigDecimal.valueOf(97);
    
    public static int checksum(String iban) {
        String tmp = (iban.substring(4) + iban.substring(0, 4)).toUpperCase();
        StringBuffer digits = new StringBuffer();
        for (int i = 0; i < tmp.length(); i++) {
            char c = tmp.charAt(i);
            if (c >= '0' && c <= '9')
                digits.append(c);
            else if (c >= 'A' && c <= 'Z') {
                int n = c - 'A' + 10;
                digits.append((char) ('0' + n / 10));
                digits.append((char) ('0' + (n % 10)));
            } else
                return -1;
        }
        BigDecimal n = new BigDecimal(digits.toString());
        int remainder = n.remainder(NINETYSEVEN).intValue();
        return remainder;
    }

    public static String fixChecksum(String ibanTemplate) {
        int remainder = checksum(ibanTemplate);
        String pp = StringUtils.leftPad(String.valueOf(98 - remainder), 2, '0');
        return ibanTemplate.substring(0, 2) + pp + ibanTemplate.substring(4);
    }

    public boolean valid(String iban) {
		if (iban == null || iban.length() < 15 || iban.length() > 32 ) {
            return false;
        }
		
	    @SuppressWarnings({"unused"}) // findbugs still flags this unused reference...
	    String countryCode = iban.substring(0, 2);
		//TODO validate country; 
	    
		int checksum = checksum(iban);
		return (checksum == 1);
	}

}
