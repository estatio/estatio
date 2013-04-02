package org.estatio.agreement;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;

@Named("Agreement Items")
@Hidden
public class AgreementItems extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "agreementitems";
    }

    public String iconName() {
        return "AgreementItem";
    }
    // }}

    // {{ newAgreementItem
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    public AgreementItem newAgreementItem(final Agreement agreement, final AgreementItemType type) {
        AgreementItem agreementItem = newTransientInstance(AgreementItem.class);
        agreementItem.setAgreement(agreement);
        agreementItem.setType(type);
        persist(agreementItem);
        agreement.addToItems(agreementItem);
        return agreementItem;
    }
    // }}

    // {{ allAgreementItems
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<AgreementItem> allAgreementItems() {
        return allInstances(AgreementItem.class);
    }
    // }}

}
