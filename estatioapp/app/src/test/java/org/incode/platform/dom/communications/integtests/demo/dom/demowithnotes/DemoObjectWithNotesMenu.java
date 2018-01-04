package org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes;

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

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "exampleDemoCommunications.DemoObjectWithNotesMenu"
)
@DomainServiceLayout(
        named = "Demo",
        menuOrder = "10.5"
)
public class DemoObjectWithNotesMenu {


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "1")
    public List<DemoObjectWithNotes> listAllDemoObjectsWithNotes() {
        return demoObjectWithNotesRepository.listAll();
    }


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "2")
    public List<DemoObjectWithNotes> findDemoObjectsWithNotesByName(
            @ParameterLayout(named="Name")
            final String name
    ) {
        return demoObjectWithNotesRepository.findByName(name);
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "3")
    public DemoObjectWithNotes createDemoObjectWithNotes(
            @ParameterLayout(named="Name")
            final String name) {
        return demoObjectWithNotesRepository.create(name);
    }


    @javax.inject.Inject
    DemoObjectWithNotesRepository demoObjectWithNotesRepository;

}
