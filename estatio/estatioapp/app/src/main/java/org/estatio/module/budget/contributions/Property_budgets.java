package org.estatio.module.budget.contributions;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;

import javax.inject.Inject;
import java.util.List;

@Mixin(method = "coll")
public class Property_budgets {

    private final Property property;

    public Property_budgets(Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.OBJECT_ONLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<Budget> coll() {
        return propertyService.budgets(this.property);
    }

    @Inject
    PropertyService propertyService;
}
