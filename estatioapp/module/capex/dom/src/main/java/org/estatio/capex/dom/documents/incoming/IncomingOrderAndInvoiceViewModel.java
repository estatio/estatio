package org.estatio.capex.dom.documents.incoming;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.tax.Tax;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@XmlTransient // so not mapped
@Setter @Getter
public class IncomingOrderAndInvoiceViewModel extends HasDocumentAbstract {

    public IncomingOrderAndInvoiceViewModel() {}

    public IncomingOrderAndInvoiceViewModel(final Document document) {
        super(document);
    }

    private Organisation buyer;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "buyer", sequence = "1")
    public IncomingOrderAndInvoiceViewModel changeBuyer(
            final Organisation buyer
    ) {
        setBuyer(buyer);
        return this;
    }

    private Organisation seller;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "seller", sequence = "1")
    public IncomingOrderAndInvoiceViewModel changeSeller(
            final Organisation seller
    ) {
        setSeller(seller);
        return this;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "seller", sequence = "2")
    public IncomingOrderAndInvoiceViewModel createSeller(
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION, optionality = Optionality.OPTIONAL) String reference,
            final boolean useNumereratorForReference,
            final String name,
            final Country country) {
        Organisation organisation = organisationRepository
                .newOrganisation(reference, useNumereratorForReference, name, country);
        setSeller(organisation);
        return this;
    }

    private String description;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "description", sequence = "1")
    public IncomingOrderAndInvoiceViewModel changeDescription(final String description){
        setDescription(description);
        return this;
    }

    private Charge charge;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "charge", sequence = "1")
    public IncomingOrderAndInvoiceViewModel findCharge(final Charge charge){
        setCharge(charge);
        return this;
    }

    public List<Charge> choices0FindCharge(){
        return chargeRepository.allIncoming();
    }

    private FixedAsset<?> fixedAsset;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "fixedAsset", sequence = "1")
    public IncomingOrderAndInvoiceViewModel findFixedAsset(final Property property){
        setFixedAsset(property);
        return this;
    }

    private Project project;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "project", sequence = "1")
    public IncomingOrderAndInvoiceViewModel findProject(final Project project){
        setProject(project);
        return this;
    }

    private String period;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "period", sequence = "1")
    public IncomingOrderAndInvoiceViewModel changePeriod(final String period){
        setPeriod(period);
        return this;
    }

    private BigDecimal netAmount;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "netAmount", sequence = "1")
    public IncomingOrderAndInvoiceViewModel changeNetAmount(final BigDecimal netAmount){
        setNetAmount(netAmount);
        calculateVat();
        determineAmounts();
        return this;
    }

    private BigDecimal vatAmount;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "vatAmount", sequence = "1")
    public IncomingOrderAndInvoiceViewModel changeVatAmount(final BigDecimal vatAmount){
        setVatAmount(vatAmount);
        calculateVat();
        determineAmounts();
        return this;
    }

    private Tax tax;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "tax", sequence = "1")
    public IncomingOrderAndInvoiceViewModel findTax(final Tax tax){
        setTax(tax);
        calculateVat();
        determineAmounts();
        return this;
    }

    private BigDecimal grossAmount;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "grossAmount", sequence = "1")
    public IncomingOrderAndInvoiceViewModel changeGrossAmount(final BigDecimal grossAmount){
        setGrossAmount(grossAmount);
        if (getNetAmount()==null){
            final BigDecimal valueToUse = getVatAmount()!=null ? grossAmount.subtract(getVatAmount()):getGrossAmount();
            setNetAmount(valueToUse);
        }
        calculateVat();
        determineAmounts();
        return this;
    }

    // ////////////////////////////////////

    @Programmatic
    void determineAmounts(){
        if (hasVat() && hasNet() && !hasGross()){
            setGrossAmount(getNetAmount().add(getVatAmount()));
            return;
        }

        if (hasVat() && hasGross() && !hasNet()){
            setNetAmount(getGrossAmount().subtract(getVatAmount()));
            return;
        }

        if (hasNet() && hasGross() && !hasVat()){
            setVatAmount(getGrossAmount().subtract(getNetAmount()));
            return;
        }

    }

    @Programmatic
    void calculateVat(){
        if (hasTax() && hasNet() && !hasVat() && !hasGross()){
            BigDecimal grossAmount = getTax().grossFromNet(getNetAmount(), clockService.now());
            setVatAmount(grossAmount.subtract(getNetAmount()));
            return;
        }
    }

    @Programmatic
    boolean hasTax(){
        return getTax()==null ? false : true;
    }
    @Programmatic
    boolean hasNet(){
        return getNetAmount()==null ? false : true;
    }
    @Programmatic
    boolean hasVat(){
        return getVatAmount()==null ? false : true;
    }
    @Programmatic
    boolean hasGross(){
        return getGrossAmount()==null ? false : true;
    }

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE) // to prevent from being shown in UI
    @Setter(AccessLevel.NONE)
    OrganisationRepository organisationRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    ChargeRepository chargeRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    ClockService clockService;

}
