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
        ChargeGroup cg = chargeRepository.newChargeGroup();
        cg.setDescription("Rent");
        cg.setReference("RENT");
        createCharge("RENT", "Rent", "IT-VATSTD");
    }

    private void createCharge(String reference, String description, String taxReference) {
        Charge c = chargeRepository.newCharge(reference);
        c.setDescription(description);
        c.setTax(taxRepository.findTaxByReference(taxReference));
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
