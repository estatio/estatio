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
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.capex.dom.project.Project;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
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
                name = "findByOrderNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE orderNumber == :orderNumber ")
})
@Unique(name = "Order_reference_UNQ", members = { "orderNumber" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.Order"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Order extends UdoDomainObject2<Order> {

    public Order() {
        super("orderNumber");
    }

    public String title() {
        return TitleBuilder.start().withReference(getOrderNumber()).toString();
    }

    public Order(
            final String orderNumber,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath,
            final String approvedBy,
            final LocalDate approvedOn) {
        this();
        this.orderNumber = orderNumber;
        this.sellerOrderReference = sellerOrderReference;
        this.entryDate = entryDate;
        this.orderDate = orderDate;
        this.seller = seller;
        this.buyer = buyer;
        this.atPath = atPath;
        this.approvedBy = approvedBy;
        this.approvedOn = approvedOn;
    }

    @Column(allowsNull = "false")
    @Getter @Setter
    private String orderNumber;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String sellerOrderReference;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate entryDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate orderDate;

    @Column(allowsNull = "false", name = "sellerId")
    @Getter @Setter
    private Party seller;

    @Column(allowsNull = "false", name = "buyerId")
    @Getter @Setter
    private Party buyer;

    @Persistent(mappedBy = "ordr", dependentElement = "true")
    @Getter @Setter
    private SortedSet<OrderItem> items = new TreeSet<>();

    @MemberOrder(name="items", sequence = "1")
    public Order addItem(
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal vatAmount,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal grossAmount,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final org.estatio.dom.asset.Property property,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Project project
    ) {
        orderItemRepository.findOrCreate(
                this, charge, description, netAmount, vatAmount, grossAmount, tax, startDate, endDate, property, project);
        // (we think there's) no need to add to the getItems(), because the item points back to this order.
        return this;
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
