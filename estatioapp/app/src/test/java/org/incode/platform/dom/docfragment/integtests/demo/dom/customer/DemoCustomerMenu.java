package org.incode.platform.dom.docfragment.integtests.demo.dom.customer;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "exampleDemoDocFragment.DemoCustomerMenu"
)
@DomainServiceLayout(
        named = "Dummy",
        menuOrder = "20.1"
)
public class DemoCustomerMenu {


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "1")
    public List<DemoCustomer> listAllDemoCustomers() {
        return demoCustomerRepository.listAll();
    }


    @javax.inject.Inject
    DemoCustomerRepository demoCustomerRepository;

}
