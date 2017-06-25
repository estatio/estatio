package org.estatio.capex.dom.order;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
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
                        + "WHERE orderNumber == :orderNumber "),
        @Query(
                name = "matchByOrderNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE orderNumber.matches(:orderNumber) "),
        @Query(
                name = "findBySeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE seller == :seller ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "orders.Order"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Order extends UdoDomainObject2<Order> {

    public Order() {
        // TODO: may need to revise this when we know more...
        super("seller, orderDate, orderNumber, id");
    }

    public Order(
            final org.estatio.dom.asset.Property property,
            final String orderNumber,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath) {
        this();
        this.property = property;
        this.orderNumber = orderNumber;
        this.sellerOrderReference = sellerOrderReference;
        this.entryDate = entryDate;
        this.orderDate = orderDate;
        this.seller = seller;
        this.buyer = buyer;
        this.atPath = atPath;
    }

    public String title() {

        final TitleBuffer buf = new TitleBuffer();

        final Optional<Document> document = lookupAttachedPdfService.lookupOrderPdfFrom(this);
        document.ifPresent(d -> buf.append(d.getName()));

        final Party seller = getSeller();
        if(seller != null) {
            buf.append(": ", seller);
        }

        final String orderNumber = getOrderNumber();
        if(orderNumber != null) {
            buf.append(", ", orderNumber);
        }

        return buf.toString();
    }

    /**
     * This relates to the owning property, while the child items may either also relate to the property,
     * or could potentially relate to individual units within the property.
     *
     * <p>
     *     This follows the same pattern as {@link IncomingInvoice}.
     * </p>
     */
    @javax.jdo.annotations.Column(name = "propertyId", allowsNull = "true")
    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private org.estatio.dom.asset.Property property;

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String orderNumber;

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String sellerOrderReference;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate entryDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate orderDate;

    @Column(allowsNull = "true", name = "sellerPartyId")
    @Getter @Setter
    private Party seller;

    @Column(allowsNull = "true", name = "buyerPartyId")
    @PropertyLayout(hidden = Where.ALL_TABLES)
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
            @Nullable final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            @Nullable final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            @Nullable final org.estatio.dom.asset.Property property,
            @Nullable final Project project,
            @Nullable final BudgetItem budgetItem
    ) {
        orderItemRepository.upsert(
                this, charge, description, netAmount, vatAmount, grossAmount, tax, startDate, endDate, property, project, budgetItem);
        // (we think there's) no need to add to the getItems(), because the item points back to this order.
        return this;
    }

    @Property(notPersisted = true)
    public BigDecimal getNetAmount() {
        return sum(OrderItem::getNetAmount);
    }

    @Property(notPersisted = true, hidden = Where.ALL_TABLES)
    public BigDecimal getVatAmount() {
        return sum(OrderItem::getVatAmount);
    }

    @Property(notPersisted = true)
    public BigDecimal getGrossAmount() {
        return sum(OrderItem::getGrossAmount);
    }

    private BigDecimal sum(final Function<OrderItem, BigDecimal> x) {
        return getItems().stream()
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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



    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

}
