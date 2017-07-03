package org.estatio.capex.dom.order;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.AbstractInterval;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.capex.dom.items.FinancialItem;
import org.estatio.capex.dom.items.FinancialItemType;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "OrderItem"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByOrderAndCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE ordr == :ordr "
                        + "   && charge == :charge "),
        @Query(
                name = "findByProjectAndCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE project == :project "
                        + "   && charge == :charge "),
        @Query(
                name = "findByCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE charge == :charge "),
        @Query(
                name = "matchByDescription", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE description.matches(:description) "),
        @Query(
                name = "findByProject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE project == :project "),
        @Query(
                name = "findByBudgetItem", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE budgetItem == :budgetItem ")
})

@Unique(name = "OrderItem_order_charge_UNQ", members = { "ordr", "charge" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "orders.OrderItem"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class OrderItem extends UdoDomainObject2<OrderItem> implements FinancialItem {

    public String title() {
        return TitleBuilder.start()
                .withName(getDescription().concat(" "))
                .withName(getNetAmount())
                .withName(" ")
                .withName(getOrdr().getSellerOrderReference().concat(" "))
                .withName(getOrdr().getOrderNumber())
                .toString();
    }

    public OrderItem() {
        super("ordr,charge");
    }

    public OrderItem(
            final Order ordr,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project,
            final BudgetItem budgetItem) {
        this();
        this.ordr = ordr;
        this.charge = charge;
        this.description = description;
        this.netAmount = netAmount;
        this.vatAmount = vatAmount;
        this.grossAmount = grossAmount;
        this.tax = tax;
        this.startDate = startDate;
        this.endDate = endDate;
        this.property = property;
        this.project = project;
        this.budgetItem = budgetItem;
    }

    /**
     * Renamed from 'order' to avoid reserve keyword issues.
     */
    @Column(allowsNull = "false", name = "orderId")
    @Getter @Setter
    @PropertyLayout(named = "order", hidden = Where.REFERENCES_PARENT)
    private Order ordr;

    @Column(allowsNull = "true", name = "chargeId")
    @Getter @Setter
    private Charge charge;

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String description;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal netAmount;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal vatAmount;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal grossAmount;

    @Column(allowsNull = "true", name = "taxId")
    @Getter @Setter
    private Tax tax;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate endDate;

    @Column(allowsNull = "true", name = "propertyId")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Property property;

    @Column(allowsNull = "true", name = "projectId")
    @Getter @Setter
    private Project project;

    @Getter @Setter
    @Column(allowsNull = "true", name="budgetItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private BudgetItem budgetItem;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify.",
            hidden = Where.EVERYWHERE
    )
    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getOrdr().getApplicationTenancy();
    }



    //region > FinancialItem impl'n (not otherwise implemented by the entity's properties)
    @Override
    @Programmatic
    public BigDecimal value() {
        return getNetAmount();
    }

    @Override
    public FinancialItemType getType() {
        return FinancialItemType.ORDERED;
    }

    @Override
    public FixedAsset<?> getFixedAsset() {
        return getProperty();
    }
    //endregion

    @Programmatic
    public String getPeriod(){
        return PeriodUtil.periodFromInterval(new LocalDateInterval(getStartDate(), getEndDate(), AbstractInterval.IntervalEnding.INCLUDING_END_DATE));
    }

    @Programmatic
    public boolean isInvoiced(){
        if (getNetAmount()==null){
            return false;
        }
        BigDecimal invoicedNetAmount = BigDecimal.ZERO;
        for (OrderItemInvoiceItemLink link : orderItemInvoiceItemLinkRepository.findByOrderItem(this)){
            if (link.getInvoiceItem().getNetAmount()!=null) {
                invoicedNetAmount = invoicedNetAmount.add(link.getInvoiceItem().getNetAmount());
            }
        }
        return invoicedNetAmount.compareTo(getNetAmount()) >= 0 ? true : false;
    }

    @Inject
    public OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
