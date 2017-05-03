package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.estatio.capex.dom.invoice.state.IncomingInvoiceState;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.task.TaskStateOwner;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        // unused since rolled-up to superclass:
        //,schema = "dbo"
        //,table = "IncomingInvoice"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(
        "capex.IncomingInvoice"
)
@Queries({
        @Query(
                name = "findByInvoiceNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber ")
})
// unused, since rolled-up
//@Unique(name = "IncomingInvoice_invoiceNumber_UNQ", members = { "invoiceNumber" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.IncomingInvoice"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class IncomingInvoice extends Invoice<IncomingInvoice> implements
        TaskStateOwner<IncomingInvoice, IncomingInvoiceState> {

    public IncomingInvoice() {
        super("invoiceNumber");
    }

    public IncomingInvoice(
            final String invoiceNumber,
            final String atPath,
            final Party buyer,
            final Party seller,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final Boolean notCorrect){
        super("invoiceNumber");
        setInvoiceNumber(invoiceNumber);
        setApplicationTenancyPath(atPath);
        setBuyer(buyer);
        setSeller(seller);
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        setPaymentMethod(paymentMethod);
        setStatus(invoiceStatus);
        setDateReceived(dateReceived);
        setIncomingInvoiceState(IncomingInvoiceState.NEW);
        setNotCorrect(notCorrect);
    }

    @MemberOrder(name="items", sequence = "1")
    public IncomingInvoice addItem(
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project
    ) {
        addItem(this, charge, description, netAmount, vatAmount, grossAmount, tax, dueDate, startDate, endDate, property, project);
        return this;
    }

    @Programmatic
    public void addItem(
            final IncomingInvoice invoice,
            // this should be an incoming charge
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Property property,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Project project
    ) {
        final BigInteger sequence = nextItemSequence();
        incomingInvoiceItemRepository.findOrCreate(
                sequence,
                invoice,
                charge,
                description,
                netAmount,
                vatAmount,
                grossAmount,
                tax,
                dueDate,
                startDate,
                endDate,
                property,
                project
        );
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private IncomingInvoiceState incomingInvoiceState;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Boolean notCorrect;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate dateReceived;

    //region > taskstate.owner
    @Override
    public IncomingInvoiceState getTaskState() {
        return getIncomingInvoiceState();
    }

    @Override
    public void setTaskState(final IncomingInvoiceState taskState) {
        setIncomingInvoiceState(taskState);
    }

    @Programmatic
    public boolean hasProject() {
        final SortedSet<IncomingInvoiceItem> items = getItemsRaw();
        for (IncomingInvoiceItem item : items) {
            final Project project = item.getProject();
            if(project != null) {
                return true;
            }
        }
        return false;
    }

    // cheating
    private SortedSet getItemsRaw() {
        return getItems();
    }

    @Programmatic
    public boolean hasFixedAsset() {
        // TODO
        return false;
    }

    //endregion

    @Inject
    private IncomingInvoiceItemRepository incomingInvoiceItemRepository;
    @Inject
    private OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
