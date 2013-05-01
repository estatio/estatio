package org.estatio.dom.asset;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.PublishedObject;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import com.danhaywood.isis.wicket.gmap3.applib.Locatable;
import com.danhaywood.isis.wicket.gmap3.applib.Location;
import com.danhaywood.isis.wicket.gmap3.service.LocationLookupService;

@PersistenceCapable
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@PublishedObject
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
public class FixedAsset extends EstatioTransactionalObject implements Comparable<FixedAsset>, Locatable {

    // {{ Reference (attribute, title)
    private String reference;

    @DescribedAs("Unique reference code for this property")
    @Unique(name = "REFERENCE_IDX")
    @Title(sequence = "1", prepend = "[", append = "] ")
    @MemberOrder(sequence = "1.1")
    @Mask("AAAAAAAA")
    public String getReference() {
        return reference;
    }

    public void setReference(final String code) {
        this.reference = code;
    }

    // }}

    // {{ Name (attribute, title)
    private String name;

    @DescribedAs("Unique reference code for this property")
    @Title(sequence = "2")
    @MemberOrder(sequence = "1.2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Location
    private Location location;

    @javax.jdo.annotations.Persistent
    @Override
    @Disabled
    @Optional
    @MemberOrder(sequence = "1.8")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @MemberOrder(sequence = "1.9")
    public FixedAsset lookupLocation(@Named("Address") String address) {
        setLocation(locationLookupService.lookup(address));
        return this;
    }

    // {{ Roles (list, unidir)
    private SortedSet<FixedAssetRole> roles = new TreeSet<FixedAssetRole>();

    @Render(Type.EAGERLY)
    @MemberOrder(name = "Roles", sequence = "2.1")
    @Persistent(mappedBy = "asset")
    public SortedSet<FixedAssetRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<FixedAssetRole> roles) {
        this.roles = roles;
    }

    @MemberOrder(name = "Roles", sequence = "1")
    public FixedAssetRole addRole(@Named("party") Party party, @Named("type") FixedAssetRoleType type, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate) {
        FixedAssetRole role = fixedAssetRolesRepo.findRole(this, party, type, startDate, endDate);
        if (role == null) {
            role = fixedAssetRolesRepo.newRole(this, party, type, startDate, endDate);
        }
        return role;
    }

    public List<Party> choices0AddRole() {
        return parties.allParties();
    }

    // }}

    
    // {{ Injected services
    private FixedAssetRoles fixedAssetRolesRepo;

    public void setFixedAssetRolesRepo(final FixedAssetRoles fixedAssetRoles) {
        this.fixedAssetRolesRepo = fixedAssetRoles;
    }

    private Parties parties;

    public void setParties(Parties parties) {
        this.parties = parties;
    }
    
    private LocationLookupService locationLookupService;

    public void setLocationLookupService(LocationLookupService locationLookupService) {
        this.locationLookupService = locationLookupService;
    }

    
    @Override
    public int compareTo(FixedAsset other) {
        return this.getName().compareTo(other.getName());
    }

    // }}

}
