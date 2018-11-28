package org.estatio.module.base.platform.applib;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.common.base.Joiner;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.dto.DtoMappingHelper;

public abstract class DtoFactoryAbstract<DO,DTO> implements DtoFactory {

    private final Class<DO> domainClass;
    private final Class<DTO> dtoClass;

    public DtoFactoryAbstract(final Class<DO> domainClass, final Class<DTO> dtoClass) {
        this.domainClass = domainClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public final boolean accepts(final Object object, final List<MediaType> acceptableMediaTypes) {
        return  domainClass.isAssignableFrom(object.getClass()) &&
                xRoDomainTypeFrom(acceptableMediaTypes).equals(dtoClass.getName());
    }

    @Override
    public final Object newDto(final Object object, final List<MediaType> acceptableMediaTypes) {
        return newDto(domainClass.cast(object));
    }

    /**
     * Mandatory hook.
     */
    protected abstract DTO newDto(final DO domainObject);


    private static String xRoDomainTypeFrom(final List<MediaType> acceptableMediaTypes) {
        for (MediaType acceptableMediaType : acceptableMediaTypes) {
            final Map<String, String> parameters = acceptableMediaType.getParameters();
            final String domainType = parameters.get("x-ro-domain-type");
            if(domainType != null) {
                return domainType;
            }
        }
        throw new IllegalArgumentException(String.format(
                "Could not locate x-ro-domain-type parameter in any of the provided media types; got: %s",
                Joiner.on(", ").join(acceptableMediaTypes)));
    }

    /**
     * For convenience of subclasses.
     */
    protected static XMLGregorianCalendar asXMLGregorianCalendar(final LocalDate date) {
        if (date == null) {
            return null;
        }
        try {
            return DatatypeFactory
                    .newInstance().newXMLGregorianCalendar(date.getYear(),date.getMonthOfYear(), date.getDayOfMonth(), 0,0,0,0,0);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();

        }
        return null;
        //return JodaLocalDateXMLGregorianCalendarAdapter.print(date);
    }

    /**
     * For convenience of subclasses.
     */
    protected static org.joda.time.LocalDate firstNonNull(final org.joda.time.LocalDate... objects){
        for (org.joda.time.LocalDate object : objects){
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    /**
     * For convenience of subclasses.
     */
    protected static BigDecimal valueElseZero(final BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    /**
     * For convenience of subclasses.
     */
    @Inject
    protected DtoMappingHelper mappingHelper;


}
