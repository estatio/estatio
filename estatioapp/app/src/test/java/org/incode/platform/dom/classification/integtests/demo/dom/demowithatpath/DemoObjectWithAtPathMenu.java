package org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath;

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
        objectType = "exampleDemoClassification.DemoObjectWithAtPathMenu"
)
@DomainServiceLayout(
        named = "Demo",
        menuOrder = "10.3"
)
public class DemoObjectWithAtPathMenu {


    //region > listAll (action)

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            bookmarking = BookmarkPolicy.AS_ROOT
    )
    @MemberOrder(sequence = "1")
    public List<DemoObjectWithAtPath> listAllDemoObjectsWithAtPath() {
        return repositoryService.allInstances(DemoObjectWithAtPath.class);
    }

    //endregion

    //region > createTopLevel (action)
    
    @MemberOrder(sequence = "2")
    public DemoObjectWithAtPath createDemoObjectWithAtPath(
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "Application tenancy")
            final String atPath) {
        final DemoObjectWithAtPath obj = new DemoObjectWithAtPath(name, atPath);
        repositoryService.persist(obj);
        return obj;
    }

    //endregion

    //region > injected services

    @javax.inject.Inject
    RepositoryService repositoryService;

    //endregion

}
