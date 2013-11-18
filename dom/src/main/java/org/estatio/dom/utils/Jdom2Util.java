/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.utils;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.joda.time.LocalDate;

import org.apache.isis.core.commons.exceptions.IsisException;

public final class Jdom2Util {

    private Jdom2Util(){}

    public static void addChild(final Element el, final String name, final Object value) {
        if(value != null) {
            el.addContent(new Element(name).setText(value.toString()));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getChild(final Element el, final String name, final Class<T> cls) {
        final Element child = el.getChild(name);
        if(child == null) { 
            return null;
        }
        final String str = child.getText();
        if (cls == String.class) {
            return (T) str;
        } else if (cls == Boolean.class) {
            return (T) new Boolean(str);
        } else if(cls == Byte.class) {
            return (T) new Byte(str);
        }else if(cls == Short.class) {
            return (T) new Short(str);
        }else if(cls == Integer.class) {
            return (T) new Integer(str);
        }else  if(cls == Long.class) {
            return (T) new Long(str);
        }else if(cls == Float.class) {
            return (T) new Float(str);
        }else if(cls == Double.class) {
            return (T) new Double(str);
        }else if(cls == BigDecimal.class) {
            return (T) new BigDecimal(str);
        }else if(cls == BigInteger.class) {
            return (T) new BigInteger(str);
        }else if(cls == LocalDate.class) { 
            return (T) new LocalDate(str);
        }else {
            throw new IllegalArgumentException("unsupported class '" + cls + "'");
        }
    }

    public static Element parse(final String xmlStr) {
        Document doc;
        try {
            doc = new SAXBuilder().build(new StringReader(xmlStr));
            final Element el = doc.getRootElement();
            return el;
        } catch (JDOMException e) {
            throw new IsisException(e);
        } catch (IOException e) {
            throw new IsisException(e);
        }
    }

    public static String toString(Element el) {
        return new XMLOutputter().outputString(el);
    }
}
