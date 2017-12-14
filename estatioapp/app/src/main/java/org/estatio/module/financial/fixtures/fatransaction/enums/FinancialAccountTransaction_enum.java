package org.estatio.module.financial.fixtures.fatransaction.enums;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;

import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.financial.fixtures.fatransaction.builders.FinancialAccountTransactionBuilder;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum FinancialAccountTransaction_enum
        implements PersonaWithBuilderScript<FinancialAccountTransaction, FinancialAccountTransactionBuilder> {

    TopModelGb_0_xactn1 (
            OrganisationAndComms_enum.TopModelGb, 0, ld(2014, 7, 1), bd(1000)
    ),
    TopModelGb_0_xactn2 (
            OrganisationAndComms_enum.TopModelGb, 0, ld(2014, 7, 2), bd(2000)
    ),
    ;

    private final OrganisationAndComms_enum party_d;
    private final int accountIndex;
    private final LocalDate date;
    private final BigDecimal amount;

    @Override
    public FinancialAccountTransactionBuilder builder() {
        return new FinancialAccountTransactionBuilder()
                .setPrereq((f,ec) -> f.setParty(f.objectFor(party_d, ec)))
                .setDate(date)
                .setAmount(amount);
    }


}
