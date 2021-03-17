package org.incode.module.country.dom.impl;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.base.dom.types.NameType;
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
                name = "State_reference_UNQ", members = "reference"),
        @javax.jdo.annotations.Unique(
                name = "State_name_UNQ", members = "name")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByCountry", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.country.dom.impl.State "
                        + "WHERE country == :country"),
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.country.dom.impl.State "
                        + "WHERE reference == :reference")
})
@DomainObject(editing = Editing.DISABLED)
public class State {

    /**
     * for testing only
     *
     * @deprecated
     */
    @Deprecated
    public State() {
    }

    public State(final String reference, final String name, final Country country ) {
        this.reference = reference;
        this.name = name;
        this.country = country;
    }


    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .withParent(getCountry())
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


    @javax.jdo.annotations.Column(allowsNull = "false", name = "countryId")
    @Getter @Setter
    private Country country;



    public static class ReferenceType {
        private ReferenceType() {}
        public static class Meta {

            public final static int MAX_LEN = 6;
            public final static String REGEX = org.incode.module.base.dom.types.ReferenceType.Meta.REGEX;

            private Meta() {}
        }
    }

}
