package org.incode.platform.dom.docfragment.integtests.dom.docfragment.dom.spiimpl;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.docfragment.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.docfragment.integtests.demo.dom.customer.DemoCustomer;
import org.incode.platform.dom.docfragment.integtests.demo.dom.invoicewithatpath.DemoInvoiceWithAtPath;

@DomainService(nature = NatureOfService.DOMAIN)
public class DemoApplicationTenancyService implements ApplicationTenancyService {

    @Override
    public String atPathFor(final Object domainObjectToRender) {
        if (domainObjectToRender instanceof DemoInvoiceWithAtPath) {
            final DemoInvoiceWithAtPath demoInvoice = (DemoInvoiceWithAtPath) domainObjectToRender;
            return demoInvoice.getAtPath();
        }
        if (domainObjectToRender instanceof DemoCustomer) {
            final DemoCustomer demoCustomer = (DemoCustomer) domainObjectToRender;
            return demoCustomer.getAtPath();
        }
        return null;
    }

}