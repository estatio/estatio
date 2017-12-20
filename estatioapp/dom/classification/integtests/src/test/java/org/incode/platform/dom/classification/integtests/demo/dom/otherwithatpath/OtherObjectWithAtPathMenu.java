package org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath;

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
        objectType = "exampleDemo.OtherObjectWithAtPathMenu"
)
@DomainServiceLayout(
        named = "Demo",
        menuOrder = "10.11"
)
public class OtherObjectWithAtPathMenu {


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "1")
    public List<OtherObjectWithAtPath> listAllOtherObjectsWithAtPath() {
        return repositoryService.allInstances(OtherObjectWithAtPath.class);
    }


    @MemberOrder(sequence = "2")
    public OtherObjectWithAtPath createOtherObjectWithAtPath(
            final String name,
            @ParameterLayout(named = "Application tenancy")
            final String atPath) {
        final OtherObjectWithAtPath obj = new OtherObjectWithAtPath(name, atPath);
        repositoryService.persist(obj);
        return obj;
    }



    @javax.inject.Inject
    RepositoryService repositoryService;


}
