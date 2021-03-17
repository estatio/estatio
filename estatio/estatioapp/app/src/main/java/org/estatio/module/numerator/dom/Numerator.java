/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.module.numerator.dom;

import java.math.BigInteger;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.FqcnType;
import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.types.ObjectIdentifierType;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyAny;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPathPersisted;

import lombok.Getter;
import lombok.Setter;

/**
 * Generates a sequence of values (eg <tt>XYZ-00101</tt>, <tt>XYZ-00102</tt>,
 * <tt>XYZ-00103</tt> etc) for a particular purpose.
 * 
 * <p>
 * A numerator is {@link #getName() named}, and this name represents the
 * purpose. For example, it could be the invoice numbers of some Agreement or
 * Property.
 * 
 * <p>
 * The numerator may be global or may be scoped to a particular object. If the
 * latter, then the {@link #getObjectType() object type} and
 * {@link #getObjectIdentifier() object identifier} identify the object to which
 * the numerator has been scoped. The values of these properties are taken from
 * the applib {@link Bookmark}.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.numerator.dom.Numerator "
                        + "WHERE name == :name "),
        @javax.jdo.annotations.Query(
                name = "findByNameAndCountry", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.numerator.dom.Numerator "
                        + "WHERE name    == :name "
                        + "   && country == :country "),
        @javax.jdo.annotations.Query(
                name = "findByNameAndCountryAndObject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.numerator.dom.Numerator "
                        + "WHERE name             == :name "
                        + "   && country          == :country "
                        + "   && objectIdentifier == :objectIdentifier "
                        + "   && objectType       == :objectType "),
        @javax.jdo.annotations.Query(
                name = "findByNameAndCountryAndObjectAndObject2", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.numerator.dom.Numerator "
                        + "WHERE name              == :name "
                        + "   && country           == :country "
                        + "   && objectIdentifier  == :objectIdentifier "
                        + "   && objectType        == :objectType "
                        + "   && objectIdentifier2 == :objectIdentifier2 "
                        + "   && objectType2       == :objectType2 "
                        )
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.numerator.Numerator"
)
public class Numerator
        extends UdoDomainObject2<Numerator>
        implements Comparable<Numerator>,  WithApplicationTenancyAny, WithApplicationTenancyPathPersisted {

    public Numerator() {
        super("name, country, objectType, objectIdentifier, objectType2, objectIdentifier2, format");
    }

    public Numerator(
            final String name,
            final Country countryIfAny,
            final String applicationTenancyPath,
            final String format,
            final BigInteger lastIncrement) {
        this();
        this.name = name;
        this.country = countryIfAny;
        this.format = format;
        this.lastIncrement = lastIncrement;
        this.applicationTenancyPath = applicationTenancyPath;
    }

    // //////////////////////////////////////

    public String title() {
        if (isScoped()) {
            return format(getLastIncrement());
        } else {
            return getName();
        }
    }

    // //////////////////////////////////////

    /**
     * The name of this numerator, for example <tt>invoice number</tt>.
     *
     * <p>
     * The combination of ({@link #getObjectType() objectType},
     * {@link #getName() name}) is unique.
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", name = "countryId")
    @Getter @Setter
    private Country country;

    // //////////////////////////////////////


    @javax.jdo.annotations.NotPersistent
    @Property(notPersisted = true)
    public boolean isScoped() {
        return getObjectType() != null;
    }

    /**
     * The {@link Bookmark#getObjectType() object type} (either the class name
     * or a unique alias of it) of the first object to which this {@link Numerator}
     * belongs.
     *
     * <p>
     * If omitted, then the {@link Numerator} is taken to be global.
     *
     * <p>
     * If present, then the {@link #getObjectIdentifier() object identifier}
     * must also be present.
     *
     * <p>
     * The ({@link #getObjectType() objectType}, {@link #getObjectIdentifier()
     * identifier}) can be used to recreate a {@link Bookmark}, if required.
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = FqcnType.Meta.MAX_LEN)
    @Getter @Setter
    private String objectType;

    public boolean hideObjectType() {
        return !isScoped();
    }

    // //////////////////////////////////////

    @NotPersistent
    public Object getObject() {
        return isScoped() ? this.bookmarkService.lookup(this::bookmark) : null;
    }

    private Bookmark bookmark() {
        return isScoped() ? new Bookmark(getObjectType(), getObjectIdentifier()) : null;
    }

    /**
     * The {@link Bookmark#getIdentifier() identifier} of the first object to which
     * this {@link Numerator} belongs.
     *
     * <p>
     * If omitted, then the {@link Numerator} is taken to be global.
     *
     * <p>
     * If present, then the {@link #getObjectType() object type} must also be
     * present.
     *
     * <p>
     * The ({@link #getObjectType() objectType}, {@link #getObjectIdentifier()
     * identifier}) can be used to recreate a {@link Bookmark}, if required.
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = ObjectIdentifierType.Meta.MAX_LEN)
    @Getter @Setter
    private String objectIdentifier;

    public boolean hideObjectIdentifier() {
        return !isScoped();
    }

    // //////////////////////////////////////


    @NotPersistent
    public Object getObject2() {
        return isScoped2() ? this.bookmarkService.lookup(this::bookmark2) : null;
    }

    private Bookmark bookmark2() {
        return isScoped2() ? new Bookmark(getObjectType2(), getObjectIdentifier2()) : null;
    }

    @javax.jdo.annotations.NotPersistent
    @Property(notPersisted = true)
    public boolean isScoped2() {
        return getObjectType2() != null;
    }

    /**
     * The {@link Bookmark#getObjectType() object type} (either the class name
     * or a unique alias of it) of the second object to which this {@link Numerator}
     * belongs.
     *
     * <p>
     * If omitted, then the {@link Numerator} is taken to be global.
     *
     * <p>
     * If present, then the {@link #getObjectIdentifier2() object identifier}
     * must also be present.
     *
     * <p>
     * The ({@link #getObjectType2() objectType}, {@link #getObjectIdentifier2()
     * identifier}) can be used to recreate a {@link Bookmark}, if required.
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = FqcnType.Meta.MAX_LEN)
    @Getter @Setter
    private String objectType2;

    public boolean hideObjectType2() {
        return !isScoped();
    }


    /**
     * The {@link Bookmark#getIdentifier() identifier} of the second object to which
     * this {@link Numerator} belongs.
     *
     * <p>
     * If omitted, then the {@link Numerator} is taken to be global.
     *
     * <p>
     * If present, then the {@link #getObjectType2() object type} must also be
     * present.
     *
     * <p>
     * The ({@link #getObjectType() objectType}, {@link #getObjectIdentifier()
     * identifier}) can be used to recreate a {@link Bookmark}, if required.
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = ObjectIdentifierType.Meta.MAX_LEN)
    @Getter @Setter
    private String objectIdentifier2;

    public boolean hideObjectIdentifier2() {
        return !isScoped2();
    }

    // //////////////////////////////////////

    @Getter @Setter
    @Column(name = "atPath")
    @Property(hidden = Where.EVERYWHERE)
    private String applicationTenancyPath;


    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }


    // //////////////////////////////////////

    /**
     * The String format to use to generate the value.
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length = FormatType.Meta.MAX_LEN)
    @Getter @Setter
    private String format;

    String format(final BigInteger n) {
        return String.format(getFormat(), n);
    }


    // //////////////////////////////////////

    /**
     * The value used by the {@link Numerator} when {@link #nextIncrementStr() return a
     * value}.
     */
    @javax.jdo.annotations.Column(allowsNull = "false")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private BigInteger lastIncrement;


    // //////////////////////////////////////

    @Programmatic
    public String nextIncrementStr() {
        return format(nextIncrement());
    }

    @Programmatic
    public String lastIncrementStr(){
        return format(getLastIncrement());
    }
    

    private BigInteger nextIncrement() {
        BigInteger last = getLastIncrement();
        if (last == null) {
            last = BigInteger.ZERO;
        }
        BigInteger next = last.add(BigInteger.ONE);
        setLastIncrement(next);
        return next;
    }

    // //////////////////////////////////////


    @NotPersistent
    @Inject
    BookmarkService bookmarkService;


    // //////////////////////////////////////

    public static class FormatType {
        private FormatType() {}
        public static class Meta {
            /**
             * {@link ReferenceType.Meta#MAX_LEN} plus a few chars
             */
            public final static int MAX_LEN = 30;
            private Meta() {}
        }
    }
}