package org.estatio.dom.geography;

import javax.jdo.annotations.DiscriminatorStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithNameUnique;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Query(name = "findGeographyByReference", language = "JDOQL", value = "SELECT FROM org.estatio.dom.geography.Geography WHERE reference == :reference") 
public abstract class Geography extends EstatioRefDataObject<Geography> implements WithReferenceComparable<Geography>, WithNameUnique {

    public Geography() {
        super("reference");
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "GEOGRAPHY_REFERENCE_UNIQUE_IDX")
    private String reference;

    /**
     * As per ISO standards for <a href=
     * "http://www.commondatahub.com/live/geography/country/iso_3166_country_codes"
     * >countries</a> and <a href=
     * "http://www.commondatahub.com/live/geography/state_province_region/iso_3166_2_state_codes"
     * >states</a>.
     */
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "GEOGRAPHY_NAME_UNIQUE_IDX")
    private String name;

    @Title
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }


}
