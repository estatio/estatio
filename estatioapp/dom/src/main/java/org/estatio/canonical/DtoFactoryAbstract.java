package org.estatio.canonical;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.LocalDate;

import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateXMLGregorianCalendarAdapter;

public class DtoFactoryAbstract {
    protected XMLGregorianCalendar convert(final LocalDate signatureDate) {
        return JodaLocalDateXMLGregorianCalendarAdapter.print(signatureDate);
    }
}
