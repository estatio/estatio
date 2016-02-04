package org.estatio.dom.budgeting.keytable;

import org.apache.isis.applib.annotation.*;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by jodo on 14/09/15.
 */
@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class KeyTableMenu {


    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<KeyTable> allKeyTables() {
        return keyTableRepository.allKeyTables();
    }


    @Inject
    private KeyTableRepository keyTableRepository;
}
