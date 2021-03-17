package org.incode.platform.dom.document.integtests.demo.dom.other;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "exampleDemoDocument.OtherObjectMenu"
)
@DomainServiceLayout(
        named = "Demo",
        menuOrder = "10.10"
)
public class OtherObjectMenu {


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "1")
    public List<OtherObject> listAllOtherObjects() {
        return repositoryService.allInstances(OtherObject.class);
    }



    @MemberOrder(sequence = "2")
    public OtherObject createOtherObjects(final String name) {
        final OtherObject obj = new OtherObject(name);
        repositoryService.persist(obj);
        return obj;
    }


    @javax.inject.Inject
    RepositoryService repositoryService;

}
