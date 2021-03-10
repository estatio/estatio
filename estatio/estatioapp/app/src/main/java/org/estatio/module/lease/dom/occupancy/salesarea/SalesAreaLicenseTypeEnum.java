package org.estatio.module.lease.dom.occupancy.salesarea;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.type.AgreementTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.type.IAgreementType;

public enum SalesAreaLicenseTypeEnum implements IAgreementType {
    SALES_AREA_LICENSE;

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementTypeServiceSupportAbstract<SalesAreaLicenseTypeEnum> {
        public SupportService() {
            super(SalesAreaLicenseTypeEnum.class);
        }
    }

}
