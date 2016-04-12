package org.estatio.canonical.financial.bankaccount.v1;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.apache.isis.applib.NonRecoverableException;

// this code will be part of Isis 1.12.0; but until then...
public class JaxbMarshaller {
    public Object fromXml(final JAXBContext jaxbContext, final String xml) {
        return fromXml(jaxbContext, xml, Maps.<String, Object>newHashMap());
    }

    public Object fromXml(
            final JAXBContext jaxbContext,
            final String xml,
            final Map<String, Object> unmarshallerProperties) {
        try {

            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            for (Map.Entry<String, Object> entry : unmarshallerProperties.entrySet()) {
                unmarshaller.setProperty(entry.getKey(), entry.getValue());
            }

            final Object unmarshal = unmarshaller.unmarshal(new StringReader(xml));
            return unmarshal;

        } catch (final JAXBException ex) {
            throw new NonRecoverableException("Error unmarshalling XML", ex);
        }
    }

    public <T> T fromXml(final Class<T> domainClass, final String xml) {
        return fromXml(domainClass, xml, Maps.<String, Object>newHashMap());
    }

    public <T> T fromXml(
            final Class<T> domainClass,
            final String xml,
            final Map<String, Object> unmarshallerProperties) {
        try {
            final JAXBContext context = JAXBContext.newInstance(domainClass);
            return (T) fromXml(context, xml, unmarshallerProperties);

        } catch (final JAXBException ex) {
            throw new NonRecoverableException("Error unmarshalling XML to class '" + domainClass.getName() + "'",
                    ex);
        }
    }

    public String toXml(final Object domainObject) {
        return toXml(domainObject, Maps.<String, Object>newHashMap());
    }

    public String toXml(final Object domainObject, final Map<String, Object> marshallerProperties) {

        final Class<?> domainClass = domainObject.getClass();
        try {
            final JAXBContext context = JAXBContext.newInstance(domainClass);

            final Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            for (Map.Entry<String, Object> entry : marshallerProperties.entrySet()) {
                marshaller.setProperty(entry.getKey(), entry.getValue());
            }

            final StringWriter sw = new StringWriter();
            marshaller.marshal(domainObject, sw);
            final String xml = sw.toString();

            return xml;

        } catch (final JAXBException ex) {
            final Class<? extends JAXBException> exClass = ex.getClass();

            final String name = exClass.getName();
            if (name.equals("com.sun.xml.bind.v2.runtime.IllegalAnnotationsException")) {
                // report a better error if possible
                // this is done reflectively so as to not have to bring in a new Maven dependency
                List<? extends Exception> errors = null;
                String annotationExceptionMessages = null;
                try {
                    final Method getErrorsMethod = exClass.getMethod("getErrors");
                    errors = (List<? extends Exception>) getErrorsMethod.invoke(ex);
                    annotationExceptionMessages = ": " + Joiner.on("; ").join(
                            Iterables.transform(errors, new Function<Exception, String>() {
                                @Override public String apply(final Exception e) {
                                    return e.getMessage();
                                }
                            }));
                } catch (Exception e) {
                    // fall through if we hit any snags, and instead throw the more generic error message.
                }
                if (errors != null) {
                    throw new NonRecoverableException(
                            "Error marshalling domain object to XML, due to illegal annotations on domain object class '"
                                    + domainClass.getName() + "'; " + errors.size() + " error"
                                    + (errors.size() == 1 ? "" : "s")
                                    + " reported" + (!errors
                                    .isEmpty() ? annotationExceptionMessages : ""), ex);
                }
            }

            throw new NonRecoverableException(
                    "Error marshalling domain object to XML; domain object class is '" + domainClass.getName()
                            + "'", ex);
        }
    }

}
