package org.estatio.dom.lease.upgrade;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainService;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;

/**
 * TODO: This method can be removed once the LeaseUpgradeService#upgradeLinkedItems has been executed in prod
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class LeaseUpgradeService extends UdoDomainService<LeaseUpgradeService>  {

    public LeaseUpgradeService() {
        super(LeaseUpgradeService.class);
    }

    public BigInteger upgradeLinkedItems() {

        BigInteger numberOfItemsLinkedIfNotAlready = BigInteger.ZERO;

        for (Lease lease : leaseRepository.allLeases()) {
            for (LeaseItem depositItem : lease.findItemsOfType(LeaseItemType.DEPOSIT)) {
                for (LeaseItem rentItem : lease.findItemsOfType(LeaseItemType.RENT)) {
                    depositItem.findOrCreateSourceItem(rentItem);
                    numberOfItemsLinkedIfNotAlready = numberOfItemsLinkedIfNotAlready.add(BigInteger.ONE);
                }

            }
            for (LeaseItem trItem : lease.findItemsOfType(LeaseItemType.TURNOVER_RENT)) {
                for (LeaseItem rentItem : lease.findItemsOfType(LeaseItemType.RENT)) {
                    trItem.findOrCreateSourceItem(rentItem);
                    numberOfItemsLinkedIfNotAlready = numberOfItemsLinkedIfNotAlready.add(BigInteger.ONE);
                }

            }
            for (LeaseItem taxItem : lease.findItemsOfType(LeaseItemType.TAX)) {
                for (LeaseItem rentItem : lease.findItemsOfType(LeaseItemType.RENT)) {
                    taxItem.findOrCreateSourceItem(rentItem);
                    numberOfItemsLinkedIfNotAlready = numberOfItemsLinkedIfNotAlready.add(BigInteger.ONE);
                }

            }
        }
        return numberOfItemsLinkedIfNotAlready;
    }

    @Inject
    private LeaseRepository leaseRepository;

}
