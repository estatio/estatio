package org.estatio.capex.dom.invoice.rule;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.task.IncomingInvoice_newTask;
import org.estatio.dom.roles.EstatioRole;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceEventService {

    public void on(IncomingInvoiceEvent event, IncomingInvoice invoice){

        // Very ugly
        switch (event){
        case CATEGORISE:
            // is this an allowed transition
            invoice.setIncomingInvoiceState(IncomingInvoiceState.CATEGORISED);
            // create tasks
            wrapperFactory.wrap(factoryService.mixin(IncomingInvoice_newTask.class, invoice))
                    .newTask(EstatioRole.PROJECT_MANAGER, "");
            break;
        case APPROVE_AS_PROJECT_MANAGER:
            invoice.setIncomingInvoiceState(IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER);
            wrapperFactory.wrap(factoryService.mixin(IncomingInvoice_newTask.class, invoice))
                    .newTask(EstatioRole.COUNTRY_DIRECTOR, "");
            break;
        case APPROVE_AS_COUNTRY_DIRECTOR:
            invoice.setIncomingInvoiceState(IncomingInvoiceState.APPROVED_BY_COUNTRY_DIRECTOR);
            break;
        case APPROVE_AS_TREASURER:
            invoice.setIncomingInvoiceState(IncomingInvoiceState.APPROVED_BY_TREASURER);
            break;
        case PAY:
            invoice.setIncomingInvoiceState(IncomingInvoiceState.PAID);
            break;
        }

    }


    @Inject FactoryService factoryService;

    @Inject WrapperFactory wrapperFactory;



}
