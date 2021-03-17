package org.estatio.module.capex.app.taskreminder;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.app.NumeratorForOrdersRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "orders.NumeratorMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY
)
public class NumeratorForOrderNumberMenu {

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public Numerator createOrderNumberNumerator(
            final Organisation buyer,
            final String format,
            final BigInteger lastValue) {
        return numeratorForOrdersRepository.findOrCreateOrderNumerator(buyer, format, lastValue);
    }

    public List<Party> choices0CreateOrderNumberNumerator() {
        return partyRepository.findByRoleTypeAndAtPath(IncomingInvoiceRoleTypeEnum.ECP, "/ITA");
    }

    public String default1CreateOrderNumberNumerator() {
        return "%04d";
    }

    public BigInteger default2CreateOrderNumberNumerator() {
        return BigInteger.ZERO;
    }




    @Inject
    NumeratorForOrdersRepository numeratorForOrdersRepository;

    @Inject
    PartyRepository partyRepository;

}
