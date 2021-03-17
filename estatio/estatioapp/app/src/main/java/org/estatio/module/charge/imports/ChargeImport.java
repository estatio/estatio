package org.estatio.module.charge.imports;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeGroupRepository;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.ChargeImport"
)
public class ChargeImport implements FixtureAwareRowHandler<ChargeImport>, ExcelFixtureRowHandler, Importable {

    public String title() {
        return "charge import";
    }

    public ChargeImport(){}

    public ChargeImport(
            final String atPath,
            final String reference,
            final String name,
            final String description,
            final String taxReference,
            final String chargeGroupReference,
            final String chargeGroupName,
            final String applicability,
            final String parent
    ){
        this();
        this.atPath = atPath;
        this.reference = reference;
        this.name = name;
        this.description = description;
        this.taxReference = taxReference;
        this.chargeGroupReference = chargeGroupReference;
        this.chargeGroupName = chargeGroupName;
        this.applicability = applicability;
        this.parent = parent;
    }

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String taxReference;

    @Getter @Setter
    private String sortOrder;

    @Getter @Setter
    private String chargeGroupReference;

    @Getter @Setter
    private String chargeGroupName;

    @Getter @Setter
    private String externalReference;

    @Getter @Setter
    private String applicability;

    @Getter @Setter
    private String parent;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    @Override
    @Programmatic
    public void handleRow(final ChargeImport previousRow) {
        importData(previousRow);
    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);

    }

    @Override
    @Programmatic
    public List<Object> importData(Object previousRow) {

        ChargeGroup chargeGroup = null;

        if (getChargeGroupReference()!=null) {
            chargeGroup = findOrCreateChargeGroup(chargeGroupReference, chargeGroupName);
        }

        final ApplicationTenancy applicationTenancy = securityApplicationTenancyRepository.findByPath(atPath);

        final Applicability applicability = this.applicability != null ? Applicability.valueOf(this.applicability) : Applicability.IN_AND_OUT;

        Tax tax = null;
        if (getTaxReference()!=null) {
            tax = taxRepository.findOrCreate(taxReference, taxReference, applicationTenancy);
        }

        if (getReference()==null){
            setReference(getName());
        }

        final Charge charge = chargeRepository.upsert(
                getReference(),
                getName(),
                getDescription(),
                applicationTenancy,
                applicability,
                tax,
                chargeGroup);

        if (getParent()!=null){
            Charge parentCharge = chargeRepository.findByReference(getParent());
            if (parentCharge!=null){
                charge.setParent(parentCharge);
            }
        }

        if (externalReference !=  null) {
            charge.setExternalReference(externalReference);
        }
        if(sortOrder != null){
            charge.setSortOrder(sortOrder);
        }

        return Lists.newArrayList(charge);
    }

    private ChargeGroup findOrCreateChargeGroup(final String reference, final String name) {
        ChargeGroup chargeGroup = chargeGroupRepository.findChargeGroup(reference);
        if (chargeGroup == null) {
            chargeGroup = chargeGroupRepository.createChargeGroup(reference, name);
        }
        chargeGroup.setName(name);
        return chargeGroup;
    }

    private <T> T wrap(final T obj) {
        return wrapperFactory.wrap(obj);
    }

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    @Inject
    private TaxRepository taxRepository;

    @Inject
    private WrapperFactory wrapperFactory;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ChargeGroupRepository chargeGroupRepository;
}
