package org.estatio.module.capex.spiimpl.docs.rml;

import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
    static String handleSrlSuffix(String str) {
        return str.endsWith("Srl") ? str.substring(0, str.length() - 3) + "S.r.l." : str;
    }

    static String stripSrlSuffix(String str) {
        return str.endsWith(" Srl") ? str.substring(0, str.length() - 4) : str;
    }

    static String capitalizeSentence(String name) {
        final String[] split = name.split("\\s");
        return Arrays.stream(split)
                .map(String::toLowerCase)
                .map(Util::capitalizeWord)
                .collect(Collectors.joining(" "));
    }

    private static String capitalizeWord(final String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
