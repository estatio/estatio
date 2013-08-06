/*
 *
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
package org.estatio.dom.numerator;

import java.math.BigInteger;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.Status;
import org.estatio.dom.WithDescriptionComparable;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.asset.Property;

@javax.jdo.annotations.PersistenceCapable(/* serializeRead = "true" */)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "NUMERATOR_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Query(
        name = "findByTypeAndProperty", language = "JDOQL", 
        value = "SELECT "
                + "FROM org.estatio.dom.numerator.Numerator "
                + "WHERE type == :type "
                + "&& property == :property")
public class Numerator extends EstatioTransactionalObject<Numerator, Status> implements Comparable<Numerator> {

    public Numerator() {
        super("type,format", Status.UNLOCKED, Status.LOCKED);
    }

    
    // //////////////////////////////////////
    

    private NumeratorType type;

    @Title(sequence="1", append=", ")
    public NumeratorType getType() {
        return type;
    }

    public void setType(final NumeratorType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private String format;
    
    @Title(sequence="2")
    public String getFormat() {
        return format;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
    

    // //////////////////////////////////////
    
    @javax.jdo.annotations.Column(name="PROPERTY_ID")
    private Property property;

    public Property getProperty() {
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private BigInteger lastIncrement;

    public BigInteger getLastIncrement() {
        return lastIncrement;
    }

    public void setLastIncrement(final BigInteger lastIncrement) {
        this.lastIncrement = lastIncrement;
    }

    // //////////////////////////////////////

    private Status status;

    @Hidden
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }
    

    // //////////////////////////////////////

    @Programmatic
    public String increment() {
        return String.format(getFormat(), incrementCounter());
    }

    private BigInteger incrementCounter() {
        BigInteger last = getLastIncrement();
        if (last == null) {
            last = BigInteger.ZERO;
        }
        BigInteger next = last.add(BigInteger.ONE);
        setLastIncrement(next);
        return next;
    }



}