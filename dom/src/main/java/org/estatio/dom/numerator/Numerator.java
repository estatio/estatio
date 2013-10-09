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

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkHolder;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;

/**
 * Generates a sequence of values (eg <tt>XYZ-00101</tt>, <tt>XYZ-00102</tt>, <tt>XYZ-00103</tt> etc)
 * for a particular purpose.
 * 
 * <p>
 * A numerator is {@link #getName() named}, and this name represents the purpose.  For example, it
 * could be the invoice numbers of some Agreement or Property.
 * 
 * <p>
 * The numerator may be global or may be scoped to a particular object.  If the latter, then the
 * {@link #getObjectType() object type} and {@link #getObjectIdentifier() object identifier} identify
 * the object to which the numerator has been scoped.  The values of these properties are taken from the
 * applib {@link Bookmark}.
 */
@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY, 
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER, 
        column = "version")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
        name = "findByNameAndObjectTypeAndObjectIdentifier", language = "JDOQL", 
        value = "SELECT "
                + "FROM org.estatio.dom.numerator.Numerator "
                + "WHERE name == :name"
                + "&& objectIdentifier == :objectIdentifier"
                + "&& objectType == :objectType"),
    @javax.jdo.annotations.Query(
        name = "findByName", language = "JDOQL", 
        value = "SELECT "
                + "FROM org.estatio.dom.numerator.Numerator "
                + "WHERE name == :name")
})
@Immutable
public class Numerator 
        extends EstatioTransactionalObject<Numerator, Status> 
        implements Comparable<Numerator>, BookmarkHolder {

    public Numerator() {
        super("name, objectType, objectIdentifier, format", Status.UNLOCKED, Status.LOCKED);
    }

    @Override
    public Status getLockable() {
        return getStatus();
    }

    @Override
    public void setLockable(final Status lockable) {
        setStatus(lockable);
    }


    // //////////////////////////////////////
    
    public String title() {
        if(isScoped()) {
            return format(getLastIncrement());
        } else {
            return getName();
        }
    }


    // //////////////////////////////////////

    @javax.jdo.annotations.NotPersistent
    @NotPersisted
    public boolean isScoped() {
        return getObjectType() != null;
    }
    
    // //////////////////////////////////////

    private String name;

    /**
     * The name of this numerator, for example <tt>invoice number</tt>.
     * 
     * <p>
     * The combination of ({@link #getObjectType() objectType}, {@link #getName() name})
     * is unique.
     */
    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public String getName() {
        return name;
    }

    public void setName(final String tagName) {
        this.name = tagName;
    }

    // //////////////////////////////////////
    
    private String objectType;

    /**
     * The {@link Bookmark#getObjectType() object type} (either the class name or a unique alias of it) 
     * of the object to which this {@link Numerator} belongs.
     * 
     * <p>
     * If omitted, then the {@link Numerator} is taken to be global.
     * 
     * <p>
     * If present, then the {@link #getObjectIdentifier() object identifier} must also be present.
     * 
     * <p>
     * The ({@link #getObjectType() objectType}, {@link #getObjectIdentifier() identifier})
     * can be used to recreate a {@link Bookmark}, if required.
     */
    @javax.jdo.annotations.Column(allowsNull="true")
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }
    public boolean hideObjectType() {
        return !isScoped();
    }

    // //////////////////////////////////////
    
    private String objectIdentifier;

    /**
     * The {@link Bookmark#getIdentifier() identifier} of the object to which this {@link Numerator} belongs.
     * 
     * <p>
     * If omitted, then the {@link Numerator} is taken to be global.
     * 
     * <p>
     * If present, then the {@link #getObjectType() object type} must also be present.
     * 
     * <p>
     * The ({@link #getObjectType() objectType}, {@link #getObjectIdentifier() identifier})
     * can be used to recreate a {@link Bookmark}, if required.
     */
    @javax.jdo.annotations.Column(allowsNull="true")
    public String getObjectIdentifier() {
        return objectIdentifier;
    }

    public void setObjectIdentifier(final String bookmark) {
        this.objectIdentifier = bookmark;
    }
    public boolean hideObjectIdentifier() {
        return !isScoped();
    }


    // //////////////////////////////////////

    private String format;

    /**
     * The String format to use to generate the value. 
     */
    @javax.jdo.annotations.Column(allowsNull="false")
    public String getFormat() {
        return format;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
    
    String format(final BigInteger n) {
        return String.format(getFormat(), n);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private BigInteger lastIncrement;

    /**
     * The value used by the {@link Numerator} when {@link #increment() return a value}.
     */
    @javax.jdo.annotations.Column(allowsNull="false")
    public BigInteger getLastIncrement() {
        return lastIncrement;
    }

    public void setLastIncrement(final BigInteger lastIncrement) {
        this.lastIncrement = lastIncrement;
    }

    // //////////////////////////////////////

    private Status status;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Hidden
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }
    

    // //////////////////////////////////////

    @Programmatic
    public String increment() {
        return format(incrementCounter());
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

    // //////////////////////////////////////

    @Programmatic
    @Override
    public Bookmark bookmark() {
        return isScoped() ? new Bookmark(getObjectType(), getObjectIdentifier()) : null;
    }


}