package org.incode.platform.dom.document.integtests.demo.dom.demowithurl;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "exampleDemoDocument.DemoObjectWithUrlMenu",
        repositoryFor = DemoObjectWithUrl.class
)
@DomainServiceLayout(
        named = "Demo",
        menuOrder = "10.6"
)
public class DemoObjectWithUrlMenu {



    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            bookmarking = BookmarkPolicy.AS_ROOT
    )
    @MemberOrder(sequence = "1")
    public List<DemoObjectWithUrl> listAllDemoObjectsWithUrl() {
        return repositoryService.allInstances(DemoObjectWithUrl.class);
    }



    @MemberOrder(sequence = "2")
    public DemoObjectWithUrl createDemoObjectWithUrl(
            @ParameterLayout(named = "Name")
            final String name) {
        final DemoObjectWithUrl obj = new DemoObjectWithUrl(name, null);
        repositoryService.persist(obj);
        return obj;
    }


    @javax.inject.Inject
    RepositoryService repositoryService;

}
