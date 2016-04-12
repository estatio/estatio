package org.estatio.canonical;

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
}
