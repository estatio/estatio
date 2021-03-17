package org.incode.module.country.dom.impl;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        , schema = "incodeCountry"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Country_reference_UNQ", members = "reference"),
        @javax.jdo.annotations.Unique(
                name = "Country_name_UNQ", members = "name"),
        @javax.jdo.annotations.Unique(
                name = "Country_alpha2Code_UNQ", members = "alpha2Code")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.country.dom.impl.Country "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "findCountryByAlpha2Code", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.country.dom.impl.Country "
                        + "WHERE alpha2Code == :alpha2Code"),
        @javax.jdo.annotations.Query(
                name = "findLikeReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.country.dom.impl.Country "
                        + "WHERE reference.matches(:reference)")
})
@DomainObject(editing = Editing.DISABLED, bounded = true)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Country  {

    /**
     * for testing only
     *
     * @deprecated
     */
    @Deprecated
    public Country() {
    }

    public Country(final String reference, final String alpha2Code, final String name) {
        setReference(reference);
        setName(name);
        setAlpha2Code(alpha2Code);
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .withReference(getReference())
                .toString();
    }

    /**
     * As per ISO standards for <a href=
     * "http://www.commondatahub.com/live/geography/country/iso_3166_country_codes"
     * >countries</a> and <a href=
     * "http://www.commondatahub.com/live/geography/state_province_region/iso_3166_2_state_codes"
     * >states</a>.
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length = ReferenceType.Meta.MAX_LEN)
    @Property(regexPattern = ReferenceType.Meta.REGEX)
    @Getter @Setter
    private String reference;


    @javax.jdo.annotations.Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Getter @Setter
    private String name;


    // not possible to make this unique because Country is rolled-up to
    // Geography.
    @javax.jdo.annotations.Column(allowsNull = "false", length = Alpha2CodeType.Meta.MAX_LEN)
    @javax.jdo.annotations.Index(unique = "false")
    @Getter @Setter
    private String alpha2Code;




    public static class Alpha2CodeType {
        private Alpha2CodeType() {}
        public static class Meta {
            public static final int MAX_LEN = 2;
            private Meta() {}
        }
    }

}
