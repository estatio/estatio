package org.estatio.module.base.platform.applib;

import java.math.BigDecimal;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.LocalDate;

public class DtoFactoryAbstract {
    protected static XMLGregorianCalendar asXMLGregorianCalendar(final LocalDate date) {
        if (date == null) {
            return null;
        }
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.getYear(),date.getMonthOfYear(), date.getDayOfMonth(), 0,0,0,0,0);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();

        }
        return null;
        //return JodaLocalDateXMLGregorianCalendarAdapter.print(date);
    }

    protected static org.joda.time.LocalDate firstNonNull(final org.joda.time.LocalDate... objects){
        for (org.joda.time.LocalDate object : objects){
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    protected static BigDecimal valueElseZero(final BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }


}
