package org.estatio.module.lease.fixtures.bankaccount.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccount_enum;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;
import org.estatio.module.lease.fixtures.bankaccount.builders.BankMandateBuilder;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.party.dom.Organisation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum BankMandate_enum implements PersonaWithFinder<BankMandate>, PersonaWithBuilderScript<BankMandate, BankMandateBuilder> {

    OxfTopModel001Gb_1  (
            Lease_enum.OxfTopModel001Gb, BankAccount_enum.TopModelGb, 1, SequenceType.FIRST, Scheme.CORE
    ),
    KalPoison001Nl_2  (
            Lease_enum.KalPoison001Nl, BankAccount_enum.PoisonNl, 2, SequenceType.FIRST, Scheme.CORE
    ),
    ;

    private final Lease_enum lease_d;
    private final BankAccount_enum bankAccount_d;
    private final int sequence;
    private final SequenceType sequenceType;
    private final Scheme scheme;

    @Override
    public BankMandate findUsing(final ServiceRegistry2 serviceRegistry) {
        final Organisation owner = lease_d.getTenant_d().findUsing(serviceRegistry);
        final String reference = BankMandateBuilder.referenceFrom(owner, sequence);
        final BankMandateRepository repository = serviceRegistry.lookupService(BankMandateRepository.class);
        return repository.findByReference(reference);
    }

    @Override
    public BankMandateBuilder builder() {
        return new BankMandateBuilder()
                .setPrereq((f,ec) -> f.setAgreement(f.objectFor(lease_d, ec)))
                .setPrereq((f,ec) -> f.setBankAccount(f.objectFor(bankAccount_d, ec)))
                .setLeaseDate(ld(2013, 10, 1))
                .setScheme(scheme)
                .setSequence(sequence)
                .setSequenceType(sequenceType);
    }


}
