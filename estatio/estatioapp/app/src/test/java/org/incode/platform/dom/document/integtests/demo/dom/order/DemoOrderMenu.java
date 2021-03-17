package org.incode.platform.dom.document.integtests.demo.dom.order;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

/**
 * As used by DocX.
 */
@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        repositoryFor = DemoOrder.class,
        objectType = "exampleDemoDocument.OrderMenu"
)
@DomainServiceLayout(
        named = "Dummy",
        menuOrder = "20.3"
)
public class DemoOrderMenu {


    //region > listAll (action)
    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            bookmarking = BookmarkPolicy.AS_ROOT
    )
    @MemberOrder(sequence = "1")
    public List<DemoOrder> listAllDemoOrders() {
        return repositoryService.allInstances(DemoOrder.class);
    }

    //endregion

    //region > create (action)
    // //////////////////////////////////////
    
    @MemberOrder(sequence = "2")
    public DemoOrder createDemoOrder(
            final String orderNumber,
            final String customerName,
            final LocalDate orderDate,
            @Nullable
            final String preferences) {
        final DemoOrder obj = new DemoOrder(orderNumber, customerName, orderDate, preferences);
        repositoryService.persist(obj);
        return obj;
    }

    //endregion

    //region > injected services
    // //////////////////////////////////////


    @Inject
    RepositoryService repositoryService;

    //endregion

}
