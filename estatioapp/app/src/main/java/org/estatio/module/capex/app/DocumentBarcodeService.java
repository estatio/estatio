package org.estatio.module.capex.app;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentBarcodeService {

    public String countryPrefixFromBarcode(final String barcodeSerie) {

        if (barcodeSerie==null || barcodeSerie.length() < 1) return null;
        String firstChar = barcodeSerie.substring(0, 1);

        switch (firstChar) {
        case "1":
            return "NL";
        case "2":
            return "IT";
        case "3":
            return "FR";
        case "4":
            return "SE";
        case "5":
            return "GB";
        case "6":
            return "BE";
        default:
            return null;
        }

    }

}
