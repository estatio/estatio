package org.estatio.module.capex.app;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

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

    @Programmatic
    public String deriveAtPathFromBarcode(final String documentName, final String fallbackAtPath) {
        final String derived = deriveAtPathFromBarcode(documentName);
        return derived != null ? derived : fallbackAtPath;
    }

    @Programmatic
    public String deriveAtPathFromBarcode(final String documentName) {
        String countryPrefix = countryPrefixFromBarcode(documentName);
        if (countryPrefix == null) return null;
        switch (countryPrefix) {
            case "FR":
                return "/FRA";
            case "BE":
                return "/BEL";
            case "IT":
                return "/ITA";
            default:
                return null;
        }
    }


    String overrideUserAtPathUsingDocumentName(
            final String atPath,
            final String documentName){

        if (!isBarcode(documentName)) {
            return atPath; // country prefix can be derived from barcodes only
        }

        return deriveAtPathFromBarcode(documentName, atPath);
    }


    boolean isBarcode(final String documentName) {
        return documentName.replace(".pdf", "").matches("\\d+");
    }

}
