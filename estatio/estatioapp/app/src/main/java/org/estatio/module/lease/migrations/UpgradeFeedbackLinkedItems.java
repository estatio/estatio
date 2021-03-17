package org.estatio.module.lease.migrations;

import java.math.BigInteger;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO: This viewmodel can be removed once the LeaseUpgradeService#upgradeLinkedItems has been executed in prod
 */
@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.lease.migrations.UpgradeFeedbackLinkedItems"
)
@Getter @Setter
public class UpgradeFeedbackLinkedItems {

    public UpgradeFeedbackLinkedItems(){ };

    public UpgradeFeedbackLinkedItems(final BigInteger numberOfItemsLinkedIfNotAlready){
        this.numberOfItemsLinkedIfNotAlready = numberOfItemsLinkedIfNotAlready;
    }

    public String title(){
        return "Upgrade linked items done";
    }

    private BigInteger numberOfItemsLinkedIfNotAlready;

}
