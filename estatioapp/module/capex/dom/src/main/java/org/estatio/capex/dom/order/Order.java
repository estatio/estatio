package org.estatio.capex.dom.order;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.time.CalendarType;
import org.estatio.capex.dom.time.TimeInterval;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.project.Project;
import org.estatio.dom.tax.Tax;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "capex",
        table = "Order"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE reference == :reference ")
})
@Unique(name = "Order_reference_UNQ", members = { "reference" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.Order"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Order extends UdoDomainObject2<Order> {

    public Order() {
        super("reference");
    }

    @Builder
    public Order(
            final String reference,
            final String number,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final TimeInterval period,
            final String sellerName,
            final Project project,
            final org.estatio.dom.asset.Property property,
            final String atPath,
            final String approvedBy,
            final LocalDate approvedOn) {
        this();
        this.reference = reference;
        this.number = number;
        this.entryDate = entryDate;
        this.orderDate = orderDate;
        this.period = period;
        this.sellerName = sellerName;
        this.project = project;
        this.property = property;
        this.atPath = atPath;
        this.approvedBy = approvedBy;
        this.approvedOn = approvedOn;
    }

    // proposal: 20160302-001
    @Column(allowsNull = "false")
    @Getter @Setter
    private String reference;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String number;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate entryDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate orderDate;

    @Column(allowsNull = "false")
    @Getter @Setter
    private TimeInterval period;

    @Column(allowsNull = "false")
    @Getter @Setter
    private String sellerName;

    @Column(allowsNull = "true")
    @Getter @Setter
    private Project project;

    @Column(allowsNull = "true")
    @Getter @Setter
    private org.estatio.dom.asset.Property property;

    @Persistent(mappedBy = "order", dependentElement = "true")
    @Getter @Setter
    private SortedSet<OrderItem> items = new TreeSet<>();

    @Programmatic
    public void addItem(
            final IncomingCharge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final CalendarType calendarType) {
        orderItemRepository.findOrCreate(
                this, charge, description, netAmount, vatAmount, grossAmount, tax, calendarType);
        // (we think there's) no need to add to the getItems(), because the item points back to this order.
    }



    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String atPath;


    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getAtPath());
    }



    // random thought: approvedBy and approvedOn are probably both attributes of a different entity, "Approval".
    // workflow is a crosscutting concern, the approval points back to the order that it approves
    // (perhaps Order is Approvable)
    @Column(allowsNull = "true")
    @Getter @Setter
    private String approvedBy;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate approvedOn;



    @Inject
    OrderItemRepository orderItemRepository;

}
