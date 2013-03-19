package org.estatio.fixture.charge;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.tax.Taxes;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class ChargeFixture extends AbstractFixture {

    @Override
    public void install() {
        createCharges();
    }

    private void createCharges() {
        createChargeAndChargeGroup("RENT", "Rent", "IT-VATSTD");
        createChargeAndChargeGroup("SERVICE_CHARGE", "Service Charge", "IT-VATSTD");
    }

    private void createChargeAndChargeGroup(String reference, String description, String taxReference) {
        ChargeGroup chargeGroup = createChargeGroup(reference, description);
        createCharge(reference, description, taxReference, chargeGroup);
    }

    private ChargeGroup createChargeGroup(String reference, String description) {
        ChargeGroup cg = chargeRepository.newChargeGroup();
        cg.setDescription(description);
        cg.setReference(reference);
        return cg;
    }

    private void createCharge(String reference, String description, String taxReference, ChargeGroup group) {
        Charge c = chargeRepository.newCharge(reference);
        c.setDescription(description);
        c.setTax(taxRepository.findTaxByReference(taxReference));
        c.setGroup(group);
    }

    private Charges chargeRepository;

    public void setChargeRepository(Charges chargeRepository) {
        this.chargeRepository = chargeRepository;
    }

    private Taxes taxRepository;

    public void setTaxRepository(Taxes taxes) {
        this.taxRepository = taxes;
    }

}
